
package com.bankguard;

// ============================================================
// BANKGUARD v1.1 - AUTOMATED INTEGRATION TESTS
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

// I use JUnit to check my BankGuard functionality.
import junit.framework.TestCase;

// I use my real BankGuard services.
import com.bankguard.database.DatabaseManager;
import com.bankguard.service.RiskService;
import com.bankguard.service.ControlService;
import com.bankguard.service.ControlTestService;
import com.bankguard.service.EvidenceService;

// I use SQLite to prepare and verify test records.
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

// I use temporary files to protect my real database.
import java.nio.file.Files;
import java.nio.file.Path;

public class AppTest extends TestCase {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private static final String DATABASE_PROPERTY =
            "bankguard.db.url";

    private Path testDatabase;
    private String previousDatabaseUrl;

    private RiskService riskService;
    private ControlService controlService;
    private ControlTestService controlTestService;
    private EvidenceService evidenceService;

    // ========================================================
    // 2. INITIALISATION BEFORE EACH TEST
    // ========================================================

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        // I remember any existing database setting.
        previousDatabaseUrl =
                System.getProperty(DATABASE_PROPERTY);

        // I create a unique temporary database file.
        testDatabase = Files.createTempFile(
                "bankguard-test-", ".db");

        // I direct BankGuard to my temporary database.
        System.setProperty(
                DATABASE_PROPERTY,
                "jdbc:sqlite:" + testDatabase.toAbsolutePath()
        );

        // I create the database tables and migrations.
        DatabaseManager manager = new DatabaseManager();

        assertTrue(
                "Test database setup failed.",
                manager.createTables()
        );

        // I prepare a fictional employee and IT risk.
        try (Connection conn = DatabaseManager.connect()) {

            try (PreparedStatement stmt =
                    conn.prepareStatement(
                    "INSERT INTO users " +
                    "(user_id, name, role) " +
                    "VALUES (101, 'Test Analyst', 'GRC Analyst')")) {

                stmt.executeUpdate();
            }

            try (PreparedStatement stmt =
                    conn.prepareStatement(
                    "INSERT INTO risks " +
                    "(risk_id, title, description, " +
                    "likelihood, impact, owner_id) " +
                    "VALUES (101, 'Test Account Takeover', " +
                    "'Simulated banking risk', 3, 5, 101)")) {

                stmt.executeUpdate();
            }
        }

        // I prepare my services for testing.
        riskService = new RiskService();
        controlService = new ControlService();
        controlTestService = new ControlTestService();
        evidenceService = new EvidenceService();
    }

    // ========================================================
    // 3. CLEANUP AFTER EACH TEST
    // ========================================================

    @Override
    protected void tearDown() throws Exception {

        try {

            // I restore the previous database setting.
            if (previousDatabaseUrl == null) {

                System.clearProperty(DATABASE_PROPERTY);

            } else {

                System.setProperty(
                        DATABASE_PROPERTY,
                        previousDatabaseUrl
                );
            }

            // I delete only the temporary test database.
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
    // 4. TESTING RISK SCORING
    // ========================================================

    public void testRiskScoreCalculation() throws Exception {

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT risk_score FROM risks " +
                     "WHERE risk_id = 101");
             ResultSet results = stmt.executeQuery()) {

            assertTrue(results.next());

            // Likelihood 3 x Impact 5 = Risk Score 15.
            assertEquals(15, results.getInt("risk_score"));
        }
    }

    // ========================================================
    // 5. TESTING INVALID RISK RATINGS
    // ========================================================

    public void testInvalidRiskRatingIsRejected() {

        String result = riskService.registerRisk(
                "Invalid Rating",
                "Likelihood exceeds the allowed scale",
                6,
                5,
                101
        );

        assertTrue(result.contains("1 to 5"));
    }

    // ========================================================
    // 6. TESTING CONTROL REGISTRATION
    // ========================================================

    public void testValidControlRegistration()
            throws Exception {

        String result = registerTestControl();

        assertTrue(result.contains(
                "Control registered successfully"));

        assertEquals(
                1,
                controlService.getControlsByRisk(101).size()
        );
    }

    // ========================================================
    // 7. TESTING INVALID CONTROL TYPES
    // ========================================================

    public void testInvalidControlTypeIsRejected()
            throws Exception {

        String result = controlService.registerControl(
                101,
                "Invalid Control",
                "Test invalid classification",
                "Unknown",
                101,
                "Daily"
        );

        assertTrue(result.contains("Invalid control type"));

        assertEquals(
                0,
                controlService.getAllControls().size()
        );
    }

    // ========================================================
    // 8. TESTING NONEXISTENT RISKS
    // ========================================================

    public void testControlRequiresExistingRisk()
            throws Exception {

        String result = controlService.registerControl(
                99999,
                "Invalid Risk Control",
                "Check risk relationships",
                "Preventive",
                101,
                "Every login"
        );

        assertTrue(result.contains("Risk not found"));
    }

    // ========================================================
    // 9. TESTING INVALID CONTROL OWNERS
    // ========================================================

    public void testControlRequiresRegisteredOwner() {

        String result = controlService.registerControl(
                101,
                "Ownership Test",
                "Check employee responsibility",
                "Preventive",
                99999,
                "Daily"
        );

        assertTrue(result.contains(
                "Control owner not found"));
    }

    // ========================================================
    // 10. TESTING CONTROL ASSESSMENTS
    // ========================================================

    public void testValidControlAssessment()
            throws Exception {

        registerTestControl();

        int controlId = getLatestId(
                "grc_controls", "control_id");

        String result = controlTestService.registerControlTest(
                controlId,
                "Review simulated MFA login records",
                "November 2026",
                "Evidence review pending",
                "Not Tested",
                101
        );

        assertTrue(result.contains(
                "Control test recorded successfully"));

        assertEquals(
                1,
                controlTestService
                        .getTestsByControl(controlId).size()
        );
    }

    // ========================================================
    // 11. TESTING INVALID CONCLUSIONS
    // ========================================================

    public void testInvalidAssessmentConclusion()
            throws Exception {

        registerTestControl();

        int controlId = getLatestId(
                "grc_controls", "control_id");

        String result = controlTestService.registerControlTest(
                controlId,
                "Review login records",
                "November 2026",
                "Testing incomplete",
                "Perfect",
                101
        );

        assertTrue(result.contains("Invalid conclusion"));

        assertEquals(
                0,
                controlTestService
                        .getTestsByControl(controlId).size()
        );
    }

    // ========================================================
    // 12. TESTING EVIDENCE REGISTRATION
    // ========================================================

    public void testEvidenceRegistration()
            throws Exception {

        registerTestControl();

        int controlId = getLatestId(
                "grc_controls", "control_id");

        controlTestService.registerControlTest(
                controlId,
                "Review simulated MFA records",
                "November 2026",
                "Review pending",
                "Not Tested",
                101
        );

        int testId = getLatestId(
                "grc_control_tests", "test_id");

        String result = evidenceService.registerEvidence(
                testId,
                "Simulated MFA Report",
                "Fictional Security Team",
                "Simulated authentication records",
                "MFA-TEST-001"
        );

        assertTrue(result.contains(
                "Evidence reference registered successfully"));

        assertEquals(
                1,
                evidenceService.getEvidenceByTest(testId).size()
        );
    }

    // ========================================================
    // 13. TESTING INVALID EVIDENCE REFERENCES
    // ========================================================

    public void testEvidenceRequiresExistingAssessment() {

        String result = evidenceService.registerEvidence(
                99999,
                "Invalid Evidence",
                "Test Source",
                "Evidence without a valid assessment",
                "TEST-999"
        );

        assertTrue(result.contains(
                "Control test not found"));
    }

    // ========================================================
    // 14. TESTING AUDIT TRAILS
    // ========================================================

    public void testRiskStatusChangeCreatesAuditRecord()
            throws Exception {

        String result = riskService.changeRiskStatus(
                101,
                "In Progress",
                101
        );

        assertTrue(result.contains("Audit record saved"));

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM audit_logs " +
                     "WHERE risk_id = 101 " +
                     "AND action = 'STATUS_CHANGED'");
             ResultSet results = stmt.executeQuery()) {

            assertTrue(results.next());

            assertEquals(1, results.getInt(1));
        }
    }

    // ========================================================
    // 15. TEST HELPERS
    // ========================================================

    private String registerTestControl() {

        // I register the same valid control whenever
        // a test needs an existing control record.
        return controlService.registerControl(
                101,
                "Multi-Factor Authentication",
                "Prevent unauthorised account access",
                "Preventive",
                101,
                "Every login"
        );
    }

    private int getLatestId(
            String tableName,
            String columnName) throws Exception {

        // I use this helper only with fixed table
        // and column names supplied by my test code.
        String sql = "SELECT MAX(" + columnName +
                ") FROM " + tableName;

        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet results = stmt.executeQuery(sql)) {

            assertTrue(results.next());

            return results.getInt(1);
        }
    }
}
