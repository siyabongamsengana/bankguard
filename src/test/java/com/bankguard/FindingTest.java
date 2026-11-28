
package com.bankguard;

// ============================================================
// BANKGUARD v1.2 - FINDING INTEGRATION TESTS
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

import junit.framework.TestCase;

import com.bankguard.database.DatabaseManager;
import com.bankguard.model.Finding;
import com.bankguard.service.FindingService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

public class FindingTest extends TestCase {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private static final String DATABASE_PROPERTY =
            "bankguard.db.url";

    private Path testDatabase;
    private String previousDatabaseUrl;

    private FindingService findingService;

    private static final int TEST_ID = 201;
    private static final int OWNER_ID = 101;

    // ========================================================
    // 2. PREPARING MY TEST DATABASE
    // ========================================================

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        // I remember the previous database setting.
        previousDatabaseUrl =
                System.getProperty(DATABASE_PROPERTY);

        // I create a temporary database for each test.
        testDatabase = Files.createTempFile(
                "bankguard-finding-test-", ".db");

        // I protect my normal BankGuard database.
        System.setProperty(
                DATABASE_PROPERTY,
                "jdbc:sqlite:" + testDatabase.toAbsolutePath()
        );

        // I create the database and apply migrations.
        DatabaseManager manager = new DatabaseManager();

        assertTrue(
                "Finding test database setup failed.",
                manager.createTables()
        );

        // I prepare fictional records for my tests.
        try (Connection conn = DatabaseManager.connect()) {

            // I register an employee responsible
            // for the finding.
            try (PreparedStatement stmt =
                    conn.prepareStatement(
                    "INSERT INTO users " +
                    "(user_id, name, role) " +
                    "VALUES (101, 'Test Analyst', 'GRC Analyst')")) {

                stmt.executeUpdate();
            }

            // I create a fictional banking risk.
            try (PreparedStatement stmt =
                    conn.prepareStatement(
                    "INSERT INTO risks " +
                    "(risk_id, title, description, " +
                    "likelihood, impact, owner_id) " +
                    "VALUES (101, 'Account Takeover', " +
                    "'Simulated banking risk', 3, 5, 101)")) {

                stmt.executeUpdate();
            }

            // I register a control linked to the risk.
            try (PreparedStatement stmt =
                    conn.prepareStatement(
                    "INSERT INTO grc_controls " +
                    "(control_id, risk_id, control_name, " +
                    "control_objective, control_type, " +
                    "owner_id, frequency) " +
                    "VALUES (101, 101, " +
                    "'Account Recovery Verification', " +
                    "'Verify customer identity', " +
                    "'Preventive', 101, 'Every reset')")) {

                stmt.executeUpdate();
            }

            // I register the control assessment
            // that will be linked to my finding.
            try (PreparedStatement stmt =
                    conn.prepareStatement(
                    "INSERT INTO grc_control_tests " +
                    "(test_id, control_id, test_procedure, " +
                    "assessment_period, test_results, " +
                    "conclusion, tested_by) " +
                    "VALUES (201, 101, " +
                    "'Test customer account recovery', " +
                    "'November 2026', " +
                    "'Weak identity verification found', " +
                    "'Ineffective', 101)")) {

                stmt.executeUpdate();
            }
        }

        // I initialise the finding service.
        findingService = new FindingService();
    }

    // ========================================================
    // 3. CLEANING UP MY TEST DATABASE
    // ========================================================

    @Override
    protected void tearDown() throws Exception {

        try {

            // I restore the original database setting.
            if (previousDatabaseUrl == null) {

                System.clearProperty(DATABASE_PROPERTY);

            } else {

                System.setProperty(
                        DATABASE_PROPERTY,
                        previousDatabaseUrl
                );
            }

            // I remove temporary test files.
            if (testDatabase != null) {

                Files.deleteIfExists(testDatabase);

                Files.deleteIfExists(Path.of(
                        testDatabase.toString() + "-wal"));

                Files.deleteIfExists(Path.of(
                        testDatabase.toString() + "-shm"));
            }

        } finally {

            super.tearDown();
        }
    }

    // ========================================================
    // 4. TESTING VALID FINDING REGISTRATION
    // ========================================================

    public void testValidFindingRegistration()
            throws Exception {

        String result = registerFinding(
                TEST_ID,
                OWNER_ID,
                "High",
                futureDate()
        );

        assertEquals(
                "Finding registered successfully!",
                result
        );

        assertEquals(
                1,
                findingService.getAllFindings().size()
        );
    }

    // ========================================================
    // 5. TESTING AN INVALID ASSESSMENT ID
    // ========================================================

    public void testFindingRequiresExistingAssessment()
            throws Exception {

        String result = registerFinding(
                99999,
                OWNER_ID,
                "High",
                futureDate()
        );

        assertTrue(result.contains(
                "Control assessment not found"));

        assertTrue(
                findingService.getAllFindings().isEmpty()
        );
    }

    // ========================================================
    // 6. TESTING INVALID FINDING OWNERS
    // ========================================================

    public void testFindingRequiresRegisteredOwner()
            throws Exception {

        String result = registerFinding(
                TEST_ID,
                99999,
                "High",
                futureDate()
        );

        assertTrue(result.contains(
                "Finding owner not found"));

        assertTrue(
                findingService.getAllFindings().isEmpty()
        );
    }

    // ========================================================
    // 7. TESTING INVALID SEVERITY
    // ========================================================

    public void testInvalidFindingSeverity()
            throws Exception {

        String result = registerFinding(
                TEST_ID,
                OWNER_ID,
                "Extreme",
                futureDate()
        );

        assertTrue(result.contains("Invalid severity"));

        assertTrue(
                findingService.getAllFindings().isEmpty()
        );
    }

    // ========================================================
    // 8. TESTING PAST REMEDIATION DEADLINES
    // ========================================================

    public void testPastDeadlineIsRejected()
            throws Exception {

        String pastDate =
                LocalDate.now().minusDays(1).toString();

        String result = registerFinding(
                TEST_ID,
                OWNER_ID,
                "High",
                pastDate
        );

        assertTrue(result.contains(
                "Remediation due date cannot be"));

        assertTrue(
                findingService.getAllFindings().isEmpty()
        );
    }

    // ========================================================
    // 9. TESTING INVALID DATE FORMATS
    // ========================================================

    public void testInvalidDeadlineFormat()
            throws Exception {

        String result = registerFinding(
                TEST_ID,
                OWNER_ID,
                "High",
                "not-a-date"
        );

        assertTrue(result.contains(
                "Invalid due date"));

        assertTrue(
                findingService.getAllFindings().isEmpty()
        );
    }

    // ========================================================
    // 10. TESTING MISSING FINDING TITLES
    // ========================================================

    public void testMissingFindingTitle()
            throws Exception {

        String result = findingService.registerFinding(
                TEST_ID,
                "",
                "Weak account recovery verification",
                "High",
                OWNER_ID,
                futureDate()
        );

        assertTrue(result.contains(
                "Finding title is required"));

        assertTrue(
                findingService.getAllFindings().isEmpty()
        );
    }

    // ========================================================
    // 11. TESTING SAVED FINDING INFORMATION
    // ========================================================

    public void testRetrieveSavedFinding()
            throws Exception {

        String result = registerFinding(
                TEST_ID,
                OWNER_ID,
                "High",
                futureDate()
        );

        assertEquals(
                "Finding registered successfully!",
                result
        );

        // I retrieve the finding from SQLite.
        List<Finding> findings =
                findingService.getAllFindings();

        assertEquals(1, findings.size());

        Finding finding = findings.get(0);

        // I check the information saved in the database.
        assertTrue(finding.getFindingId() > 0);

        assertEquals(TEST_ID, finding.getTestId());

        assertEquals(
                "Weak Account Recovery Verification",
                finding.getFindingTitle()
        );

        assertEquals("High", finding.getSeverity());

        assertEquals(OWNER_ID, finding.getOwnerId());

        assertEquals(futureDate(), finding.getDueDate());

        assertEquals("Open", finding.getStatus());

        assertNotNull(finding.getCreatedAt());
    }

    // ========================================================
    // 12. PREPARING VALID FINDING INFORMATION
    // ========================================================

    private String registerFinding(
            int testId,
            int ownerId,
            String severity,
            String dueDate) {

        // I use the same fictional banking scenario
        // throughout my automated tests.

        return findingService.registerFinding(
                testId,
                "Weak Account Recovery Verification",
                "An employee can reset customer passwords " +
                "using publicly available information.",
                severity,
                ownerId,
                dueDate
        );
    }

    // ========================================================
    // 13. PREPARING A FUTURE DEADLINE
    // ========================================================

    private String futureDate() {

        // I use a future date so my automated tests
        // continue working in later months.

        return LocalDate.now()
                .plusDays(30)
                .toString();
    }
}
