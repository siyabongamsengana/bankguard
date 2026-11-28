
package com.bankguard;

// ============================================================
// BANKGUARD v1.2 - REMEDIATION ACTION INTEGRATION TESTS
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

import junit.framework.TestCase;

import com.bankguard.database.DatabaseManager;
import com.bankguard.model.RemediationAction;
import com.bankguard.service.RemediationActionService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

public class RemediationActionTest extends TestCase {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private static final String DATABASE_PROPERTY =
            "bankguard.db.url";

    private static final int FINDING_ID = 301;
    private static final int EMPLOYEE_ID = 101;

    private Path testDatabase;
    private String previousDatabaseUrl;

    private RemediationActionService actionService;

    // ========================================================
    // 2. PREPARING MY TEST DATABASE
    // ========================================================

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        // I remember my previous database setting.
        previousDatabaseUrl =
                System.getProperty(DATABASE_PROPERTY);

        // I create a separate database for every test.
        testDatabase = Files.createTempFile(
                "bankguard-remediation-test-", ".db");

        // I protect my real bankguard.db.
        System.setProperty(
                DATABASE_PROPERTY,
                "jdbc:sqlite:" + testDatabase.toAbsolutePath()
        );

        // I create my database tables and migrations.
        DatabaseManager manager = new DatabaseManager();

        assertTrue(
                "Remediation test database setup failed.",
                manager.createTables()
        );

        // I prepare fictional banking records.
        try (Connection conn = DatabaseManager.connect()) {

            // I register an employee.
            try (PreparedStatement stmt =
                    conn.prepareStatement(
                    "INSERT INTO users " +
                    "(user_id, name, role) " +
                    "VALUES (101, 'Remediation Owner', " +
                    "'IT Operations Manager')")) {

                stmt.executeUpdate();
            }

            // I create an account takeover risk.
            try (PreparedStatement stmt =
                    conn.prepareStatement(
                    "INSERT INTO risks " +
                    "(risk_id, title, description, " +
                    "likelihood, impact, owner_id) " +
                    "VALUES (101, 'Account Takeover', " +
                    "'Simulated banking risk', 3, 5, 101)")) {

                stmt.executeUpdate();
            }

            // I register an account recovery control.
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

            // I register an assessment that identifies
            // a weakness in account recovery.
            try (PreparedStatement stmt =
                    conn.prepareStatement(
                    "INSERT INTO grc_control_tests " +
                    "(test_id, control_id, test_procedure, " +
                    "assessment_period, test_results, " +
                    "conclusion, tested_by) " +
                    "VALUES (201, 101, " +
                    "'Test account recovery', " +
                    "'November 2026', " +
                    "'Weak verification identified', " +
                    "'Ineffective', 101)")) {

                stmt.executeUpdate();
            }

            // I create a finding linked to the test.
            try (PreparedStatement stmt =
                    conn.prepareStatement(
                    "INSERT INTO grc_findings " +
                    "(finding_id, test_id, finding_title, " +
                    "finding_description, severity, " +
                    "owner_id, due_date) " +
                    "VALUES (301, 201, " +
                    "'Weak Account Recovery', " +
                    "'Identity checks can be bypassed', " +
                    "'High', 101, ?)")) {

                stmt.setString(1, futureDate());
                stmt.executeUpdate();
            }
        }

        // I prepare the service I want to test.
        actionService = new RemediationActionService();
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

            // I delete only the temporary database.
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
    // 4. TESTING VALID ACTION REGISTRATION
    // ========================================================

    public void testValidRemediationAction()
            throws Exception {

        String result = registerAction(
                FINDING_ID,
                EMPLOYEE_ID,
                "Strengthen customer identity verification",
                futureDate()
        );

        assertEquals(
                "Remediation action registered successfully!",
                result
        );

        assertEquals(
                1,
                actionService
                        .getActionsByFinding(FINDING_ID)
                        .size()
        );
    }

    // ========================================================
    // 5. TESTING AN INVALID FINDING
    // ========================================================

    public void testActionRequiresExistingFinding()
            throws Exception {

        String result = registerAction(
                99999,
                EMPLOYEE_ID,
                "Strengthen verification",
                futureDate()
        );

        assertTrue(result.contains("Finding not found"));

        assertTrue(
                actionService
                        .getActionsByFinding(FINDING_ID)
                        .isEmpty()
        );
    }

    // ========================================================
    // 6. TESTING AN INVALID EMPLOYEE
    // ========================================================

    public void testActionRequiresRegisteredEmployee()
            throws Exception {

        String result = registerAction(
                FINDING_ID,
                99999,
                "Strengthen verification",
                futureDate()
        );

        assertTrue(result.contains(
                "Assigned employee not found"));

        assertTrue(
                actionService
                        .getActionsByFinding(FINDING_ID)
                        .isEmpty()
        );
    }

    // ========================================================
    // 7. TESTING A MISSING DESCRIPTION
    // ========================================================

    public void testMissingActionDescription()
            throws Exception {

        String result = registerAction(
                FINDING_ID,
                EMPLOYEE_ID,
                "",
                futureDate()
        );

        assertTrue(result.contains(
                "Action description is required"));

        assertTrue(
                actionService
                        .getActionsByFinding(FINDING_ID)
                        .isEmpty()
        );
    }

    // ========================================================
    // 8. TESTING A PAST DEADLINE
    // ========================================================

    public void testPastActionDeadline()
            throws Exception {

        String pastDate =
                LocalDate.now().minusDays(1).toString();

        String result = registerAction(
                FINDING_ID,
                EMPLOYEE_ID,
                "Strengthen verification",
                pastDate
        );

        assertTrue(result.contains(
                "Action due date cannot be in the past"));

        assertTrue(
                actionService
                        .getActionsByFinding(FINDING_ID)
                        .isEmpty()
        );
    }

    // ========================================================
    // 9. TESTING AN INVALID DATE
    // ========================================================

    public void testInvalidActionDate()
            throws Exception {

        String result = registerAction(
                FINDING_ID,
                EMPLOYEE_ID,
                "Strengthen verification",
                "not-a-date"
        );

        assertTrue(result.contains(
                "Invalid due date"));

        assertTrue(
                actionService
                        .getActionsByFinding(FINDING_ID)
                        .isEmpty()
        );
    }

    // ========================================================
    // 10. TESTING MULTIPLE CORRECTIVE ACTIONS
    // ========================================================

    public void testMultipleActionsForOneFinding()
            throws Exception {

        String firstResult = registerAction(
                FINDING_ID,
                EMPLOYEE_ID,
                "Improve identity verification rules",
                futureDate()
        );

        String secondResult = registerAction(
                FINDING_ID,
                EMPLOYEE_ID,
                "Train account recovery employees",
                futureDate()
        );

        assertTrue(firstResult.contains(
                "Remediation action registered successfully"));

        assertTrue(secondResult.contains(
                "Remediation action registered successfully"));

        // I confirm both actions belong to one finding.
        assertEquals(
                2,
                actionService
                        .getActionsByFinding(FINDING_ID)
                        .size()
        );
    }

    // ========================================================
    // 11. TESTING SAVED ACTION DETAILS
    // ========================================================

    public void testRetrieveRemediationAction()
            throws Exception {

        String description =
                "Strengthen customer identity verification";

        String result = registerAction(
                FINDING_ID,
                EMPLOYEE_ID,
                description,
                futureDate()
        );

        assertEquals(
                "Remediation action registered successfully!",
                result
        );

        List<RemediationAction> actions =
                actionService.getActionsByFinding(FINDING_ID);

        assertEquals(1, actions.size());

        RemediationAction action = actions.get(0);

        // I verify that SQLite saved the correct values.
        assertTrue(action.getActionId() > 0);

        assertEquals(
                FINDING_ID,
                action.getFindingId()
        );

        assertEquals(
                description,
                action.getActionDescription()
        );

        assertEquals(
                EMPLOYEE_ID,
                action.getAssignedTo()
        );

        assertEquals(
                futureDate(),
                action.getDueDate()
        );

        assertEquals(
                "Open",
                action.getStatus()
        );

        assertNull(action.getCompletionEvidence());

        assertNotNull(action.getCreatedAt());
    }

    // ========================================================
    // 12. REGISTERING ACTIONS FOR MY TESTS
    // ========================================================

    private String registerAction(
            int findingId,
            int assignedTo,
            String description,
            String dueDate) {

        // I use a fictional banking remediation scenario.
        return actionService.registerAction(
                findingId,
                description,
                assignedTo,
                dueDate
        );
    }

    // ========================================================
    // 13. PREPARING A FUTURE DEADLINE
    // ========================================================

    private String futureDate() {

        // I keep deadlines in the future so the tests
        // continue working after November 2026.
        return LocalDate.now()
                .plusDays(30)
                .toString();
    }
}
