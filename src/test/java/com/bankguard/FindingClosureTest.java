
package com.bankguard;

// ============================================================
// BANKGUARD v1.2 - FINDING CLOSURE INTEGRATION TESTS
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

import junit.framework.TestCase;

import com.bankguard.database.DatabaseManager;
import com.bankguard.model.RemediationAction;
import com.bankguard.service.RemediationActionService;
import com.bankguard.service.RemediationVerificationService;
import com.bankguard.service.FindingClosureService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

// ============================================================
// FINDING CLOSURE TEST CLASS
// ============================================================

public class FindingClosureTest extends TestCase {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private static final String DATABASE_PROPERTY =
            "bankguard.db.url";

    private static final int FINDING_ID = 301;
    private static final int OWNER_ID = 101;
    private static final int ASSIGNEE_ID = 102;
    private static final int REVIEWER_ID = 103;
    private static final int CLOSURE_EMPLOYEE_ID = 104;

    private Path testDatabase;
    private String previousDatabaseUrl;

    private RemediationActionService actionService;
    private RemediationVerificationService verificationService;
    private FindingClosureService closureService;

    // ========================================================
    // 2. PREPARING MY TEST DATABASE
    // ========================================================

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        // I remember the original database setting.
        previousDatabaseUrl =
                System.getProperty(DATABASE_PROPERTY);

        // I create a temporary SQLite database.
        testDatabase = Files.createTempFile(
                "bankguard-closure-test-", ".db");

        // I protect the normal bankguard.db file.
        System.setProperty(
                DATABASE_PROPERTY,
                "jdbc:sqlite:" + testDatabase.toAbsolutePath()
        );

        // I create all tables and migrations.
        DatabaseManager manager = new DatabaseManager();

        assertTrue(
                "Finding closure test database setup failed.",
                manager.createTables()
        );

        // I prepare the fictional banking records.
        try (Connection conn = DatabaseManager.connect()) {

            // I register four employees with different
            // responsibilities in the GRC process.
            try (PreparedStatement stmt =
                    conn.prepareStatement(
                    "INSERT INTO users " +
                    "(user_id, name, role) VALUES (?, ?, ?)")) {

                insertEmployee(
                        stmt, OWNER_ID,
                        "Finding Owner", "GRC Analyst");

                insertEmployee(
                        stmt, ASSIGNEE_ID,
                        "Remediation Employee", "IT Operations");

                insertEmployee(
                        stmt, REVIEWER_ID,
                        "Independent Reviewer", "GRC Manager");

                insertEmployee(
                        stmt, CLOSURE_EMPLOYEE_ID,
                        "Closure Officer", "Risk Manager");
            }

            // I create a fictional account takeover risk.
            try (PreparedStatement stmt =
                    conn.prepareStatement(
                    "INSERT INTO risks " +
                    "(risk_id, title, description, " +
                    "likelihood, impact, owner_id) " +
                    "VALUES (101, ?, ?, 3, 5, 101)")) {

                stmt.setString(1, "Account Takeover");

                stmt.setString(2,
                        "Simulated banking security risk");

                stmt.executeUpdate();
            }

            // I register the account recovery control.
            try (PreparedStatement stmt =
                    conn.prepareStatement(
                    "INSERT INTO grc_controls " +
                    "(control_id, risk_id, control_name, " +
                    "control_objective, control_type, " +
                    "owner_id, frequency) " +
                    "VALUES (101, 101, ?, ?, " +
                    "'Preventive', 101, 'Every reset')")) {

                stmt.setString(1,
                        "Account Recovery Verification");

                stmt.setString(2,
                        "Verify customer identity");

                stmt.executeUpdate();
            }

            // I record an ineffective control assessment.
            try (PreparedStatement stmt =
                    conn.prepareStatement(
                    "INSERT INTO grc_control_tests " +
                    "(test_id, control_id, test_procedure, " +
                    "assessment_period, test_results, " +
                    "conclusion, tested_by) " +
                    "VALUES (201, 101, ?, ?, ?, " +
                    "'Ineffective', 101)")) {

                stmt.setString(1,
                        "Test customer account recovery");

                stmt.setString(2,
                        "November 2026");

                stmt.setString(3,
                        "Weak verification identified");

                stmt.executeUpdate();
            }

            // I record the weakness as a finding.
            try (PreparedStatement stmt =
                    conn.prepareStatement(
                    "INSERT INTO grc_findings " +
                    "(finding_id, test_id, finding_title, " +
                    "finding_description, severity, " +
                    "owner_id, due_date) " +
                    "VALUES (301, 201, ?, ?, " +
                    "'High', 101, ?)")) {

                stmt.setString(1,
                        "Weak Account Recovery");

                stmt.setString(2,
                        "Public information can be used " +
                        "to reset customer passwords");

                stmt.setString(3, futureDate());

                stmt.executeUpdate();
            }
        }

        // I initialise the services I want to test.
        actionService = new RemediationActionService();

        verificationService =
                new RemediationVerificationService();

        closureService = new FindingClosureService();
    }

    // ========================================================
    // 3. CLEANING UP AFTER EACH TEST
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
                        previousDatabaseUrl);
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
    // 4. TESTING SUCCESSFUL FINDING CLOSURE
    // ========================================================

    public void testSuccessfulFindingClosure()
            throws Exception {

        // I register and complete corrective work.
        int actionId = createAction();

        completeAction(actionId);

        // I obtain independent verification.
        verifyFinding("Verified");

        // I close the finding with written reasons.
        String result = closeFinding();

        assertTrue(result.contains(
                "Finding closed successfully"));

        // I confirm the finding is actually closed.
        assertEquals("Closed", getFindingStatus());

        // I confirm one closure record was saved.
        assertEquals(1, getClosureCount());

        // I confirm the closure references
        // the successful verification.
        assertEquals(
                "Verified",
                getClosureVerificationDecision()
        );

        // I confirm the closure employee was recorded.
        assertEquals(
                CLOSURE_EMPLOYEE_ID,
                getClosureEmployeeId()
        );
    }

    // ========================================================
    // 5. PREVENTING CLOSURE WITHOUT REMEDIATION
    // ========================================================

    public void testClosureRequiresRemediationActions()
            throws Exception {

        // I create no corrective action.
        String result = closeFinding();

        assertTrue(result.contains(
                "at least one remediation action"));

        assertEquals("Open", getFindingStatus());

        assertEquals(0, getClosureCount());
    }

    // ========================================================
    // 6. PREVENTING CLOSURE WITH INCOMPLETE ACTIONS
    // ========================================================

    public void testIncompleteActionBlocksClosure()
            throws Exception {

        // I register corrective work
        // but deliberately leave it open.
        createAction();

        String result = closeFinding();

        assertTrue(result.contains(
                "All remediation actions must be completed"));

        assertEquals("Open", getFindingStatus());

        assertEquals(0, getClosureCount());
    }

    // ========================================================
    // 7. REQUIRING INDEPENDENT VERIFICATION
    // ========================================================

    public void testClosureRequiresVerification()
            throws Exception {

        int actionId = createAction();

        completeAction(actionId);

        // I deliberately skip the independent review.
        String result = closeFinding();

        assertTrue(result.contains(
                "Independent verification is required"));

        assertEquals("Open", getFindingStatus());

        assertEquals(0, getClosureCount());
    }

    // ========================================================
    // 8. PREVENTING CLOSURE AFTER REJECTED VERIFICATION
    // ========================================================

    public void testRejectedVerificationBlocksClosure()
            throws Exception {

        int actionId = createAction();

        completeAction(actionId);

        // I record a rejected verification decision.
        String reviewResult = verifyFinding("Rejected");

        assertTrue(reviewResult.contains(
                "Decision: Rejected"));

        String result = closeFinding();

        assertTrue(result.contains(
                "must be Verified"));

        assertEquals("Open", getFindingStatus());

        assertEquals(0, getClosureCount());
    }

    // ========================================================
    // 9. REJECTING UNREGISTERED CLOSURE EMPLOYEES
    // ========================================================

    public void testUnregisteredEmployeeCannotCloseFinding()
            throws Exception {

        int actionId = createAction();

        completeAction(actionId);

        verifyFinding("Verified");

        // I attempt closure with an unknown employee.
        String result = closureService.closeFinding(
                FINDING_ID,
                99999,
                "The control weakness has been resolved"
        );

        assertTrue(result.contains(
                "Closure employee is not registered"));

        assertEquals("Open", getFindingStatus());

        assertEquals(0, getClosureCount());
    }

    // ========================================================
    // 10. REQUIRING WRITTEN CLOSURE REASONS
    // ========================================================

    public void testMissingClosureNotes()
            throws Exception {

        int actionId = createAction();

        completeAction(actionId);

        verifyFinding("Verified");

        // I deliberately omit the closure justification.
        String result = closureService.closeFinding(
                FINDING_ID,
                CLOSURE_EMPLOYEE_ID,
                ""
        );

        assertTrue(result.contains(
                "Closure notes are required"));

        assertEquals("Open", getFindingStatus());

        assertEquals(0, getClosureCount());
    }

    // ========================================================
    // 11. PREVENTING DUPLICATE FINDING CLOSURE
    // ========================================================

    public void testDuplicateClosureIsRejected()
            throws Exception {

        int actionId = createAction();

        completeAction(actionId);

        verifyFinding("Verified");

        // I close the finding successfully.
        String firstResult = closeFinding();

        assertTrue(firstResult.contains(
                "Finding closed successfully"));

        // I attempt to close the same finding again.
        String secondResult = closeFinding();

        assertTrue(secondResult.contains(
                "Finding is already closed"));

        // I confirm the original record remains intact.
        assertEquals("Closed", getFindingStatus());

        assertEquals(1, getClosureCount());
    }

    // ========================================================
    // 12. INSERTING MY FICTIONAL EMPLOYEES
    // ========================================================

    private void insertEmployee(
            PreparedStatement stmt,
            int employeeId,
            String name,
            String role)
            throws Exception {

        stmt.setInt(1, employeeId);
        stmt.setString(2, name);
        stmt.setString(3, role);

        stmt.executeUpdate();
    }

    // ========================================================
    // 13. CREATING A CORRECTIVE ACTION
    // ========================================================

    private int createAction() throws Exception {

        String result = actionService.registerAction(
                FINDING_ID,
                "Strengthen customer identity verification",
                ASSIGNEE_ID,
                futureDate()
        );

        assertTrue(result.contains(
                "Remediation action registered successfully"));

        RemediationAction action =
                actionService.getActionsByFinding(
                        FINDING_ID).get(0);

        return action.getActionId();
    }

    // ========================================================
    // 14. COMPLETING MY CORRECTIVE ACTION
    // ========================================================

    private void completeAction(int actionId) {

        String result = actionService.completeAction(
                actionId,
                ASSIGNEE_ID,
                "RECOVERY-FIX-001"
        );

        assertTrue(result.contains(
                "Remediation action marked as completed"));
    }

    // ========================================================
    // 15. RECORDING AN INDEPENDENT VERIFICATION
    // ========================================================

    private String verifyFinding(String decision) {

        return verificationService.verifyFinding(
                FINDING_ID,
                REVIEWER_ID,
                decision,
                "Simulated evidence was reviewed",
                "The remediation was assessed independently"
        );
    }

    // ========================================================
    // 16. RECORDING A FORMAL CLOSURE DECISION
    // ========================================================

    private String closeFinding() {

        return closureService.closeFinding(
                FINDING_ID,
                CLOSURE_EMPLOYEE_ID,
                "Corrective work was completed and " +
                "independently verified"
        );
    }

    // ========================================================
    // 17. CHECKING THE SAVED FINDING STATUS
    // ========================================================

    private String getFindingStatus() throws Exception {

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                    conn.prepareStatement(
                    "SELECT status FROM grc_findings " +
                    "WHERE finding_id = ?")) {

            stmt.setInt(1, FINDING_ID);

            try (ResultSet results = stmt.executeQuery()) {

                assertTrue(results.next());

                return results.getString("status");
            }
        }
    }

    // ========================================================
    // 18. COUNTING FORMAL CLOSURE RECORDS
    // ========================================================

    private int getClosureCount() throws Exception {

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                    conn.prepareStatement(
                    "SELECT COUNT(*) FROM grc_finding_closures " +
                    "WHERE finding_id = ?")) {

            stmt.setInt(1, FINDING_ID);

            try (ResultSet results = stmt.executeQuery()) {

                assertTrue(results.next());

                return results.getInt(1);
            }
        }
    }

    // ========================================================
    // 19. CHECKING THE CLOSURE VERIFICATION
    // ========================================================

    private String getClosureVerificationDecision()
            throws Exception {

        String sql =
                "SELECT v.decision " +
                "FROM grc_finding_closures c " +
                "JOIN grc_remediation_verifications v " +
                "ON c.verification_id = v.verification_id " +
                "WHERE c.finding_id = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                    conn.prepareStatement(sql)) {

            stmt.setInt(1, FINDING_ID);

            try (ResultSet results = stmt.executeQuery()) {

                assertTrue(results.next());

                return results.getString("decision");
            }
        }
    }

    // ========================================================
    // 20. CHECKING THE EMPLOYEE WHO CLOSED THE FINDING
    // ========================================================

    private int getClosureEmployeeId()
            throws Exception {

        String sql =
                "SELECT closed_by " +
                "FROM grc_finding_closures " +
                "WHERE finding_id = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                    conn.prepareStatement(sql)) {

            stmt.setInt(1, FINDING_ID);

            try (ResultSet results = stmt.executeQuery()) {

                assertTrue(results.next());

                return results.getInt("closed_by");
            }
        }
    }

    // ========================================================
    // 21. PREPARING A FUTURE DEADLINE
    // ========================================================

    private String futureDate() {

        return LocalDate.now()
                .plusDays(30)
                .toString();
    }
}
