
package com.bankguard;

// ============================================================
// BANKGUARD v1.2 - REMEDIATION VERIFICATION TESTS
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

import junit.framework.TestCase;

import com.bankguard.database.DatabaseManager;
import com.bankguard.model.RemediationVerification;
import com.bankguard.model.RemediationAction;
import com.bankguard.service.RemediationVerificationService;
import com.bankguard.service.RemediationActionService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;

public class RemediationVerificationTest extends TestCase {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private static final String DATABASE_PROPERTY =
            "bankguard.db.url";

    private static final int FINDING_ID = 301;
    private static final int OWNER_ID = 101;
    private static final int ASSIGNEE_ID = 102;
    private static final int REVIEWER_ID = 103;

    private Path testDatabase;
    private String previousDatabaseUrl;

    private RemediationVerificationService verificationService;
    private RemediationActionService actionService;

    // ========================================================
    // 2. PREPARING MY TEST DATABASE
    // ========================================================

    @Override
    protected void setUp() throws Exception {

        super.setUp();

        // I remember the previous database setting.
        previousDatabaseUrl =
                System.getProperty(DATABASE_PROPERTY);

        // I create a temporary SQLite database.
        testDatabase = Files.createTempFile(
                "bankguard-verification-", ".db");

        // I keep test records separate from bankguard.db.
        System.setProperty(
                DATABASE_PROPERTY,
                "jdbc:sqlite:" + testDatabase.toAbsolutePath()
        );

        // I prepare all the existing database tables.
        DatabaseManager manager = new DatabaseManager();

        assertTrue(
                "Verification test database setup failed.",
                manager.createTables()
        );

        // I create fictional employees and banking records.
        try (Connection conn = DatabaseManager.connect()) {

            // I separate the finding owner,
            // remediation employee and independent reviewer.
            try (PreparedStatement stmt =
                    conn.prepareStatement(
                    "INSERT INTO users " +
                    "(user_id, name, role) VALUES (?, ?, ?)")) {

                insertEmployee(stmt, 101,
                        "Finding Owner", "GRC Analyst");

                insertEmployee(stmt, 102,
                        "Remediation Employee", "IT Operations");

                insertEmployee(stmt, 103,
                        "Independent Reviewer", "GRC Manager");
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
                        "Simulated customer account risk");

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

            // I record a failed control assessment.
            try (PreparedStatement stmt =
                    conn.prepareStatement(
                    "INSERT INTO grc_control_tests " +
                    "(test_id, control_id, test_procedure, " +
                    "assessment_period, test_results, " +
                    "conclusion, tested_by) " +
                    "VALUES (201, 101, ?, ?, ?, " +
                    "'Ineffective', 101)")) {

                stmt.setString(1,
                        "Test account recovery");

                stmt.setString(2,
                        "November 2026");

                stmt.setString(3,
                        "Weak identity verification found");

                stmt.executeUpdate();
            }

            // I create a finding linked to the failed test.
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

        // I prepare the services being tested.
        verificationService =
                new RemediationVerificationService();

        actionService = new RemediationActionService();
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

            // I remove only temporary test files.
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
    // 4. TESTING SUCCESSFUL INDEPENDENT VERIFICATION
    // ========================================================

    public void testSuccessfulVerification()
            throws Exception {

        int actionId = createAction();

        completeAction(actionId);

        String result = verify(
                REVIEWER_ID,
                "Verified"
        );

        assertTrue(result.contains(
                "Remediation verification recorded successfully"));

        List<RemediationVerification> history =
                verificationService.getVerificationHistory(
                        FINDING_ID);

        assertEquals(1, history.size());

        RemediationVerification verification =
                history.get(0);

        assertEquals(
                "Verified",
                verification.getDecision());

        assertEquals(
                REVIEWER_ID,
                verification.getReviewerId());

        assertNotNull(
                verification.getVerifiedAt());

        // I confirm verification does not automatically
        // close the original finding.
        assertEquals("Open", getFindingStatus());
    }

    // ========================================================
    // 5. REQUIRING A REMEDIATION ACTION
    // ========================================================

    public void testVerificationRequiresAction()
            throws Exception {

        // I deliberately create no corrective action.
        String result = verify(
                REVIEWER_ID,
                "Verified"
        );

        assertTrue(result.contains(
                "at least one remediation action"));

        assertEquals(0, verificationCount());
    }

    // ========================================================
    // 6. PREVENTING VERIFICATION OF INCOMPLETE WORK
    // ========================================================

    public void testIncompleteActionBlocksVerification()
            throws Exception {

        createAction();

        // I have not completed the corrective work.
        String result = verify(
                REVIEWER_ID,
                "Verified"
        );

        assertTrue(result.contains(
                "All remediation actions must be completed"));

        assertEquals(0, verificationCount());
    }

    // ========================================================
    // 7. REQUIRING COMPLETION EVIDENCE
    // ========================================================

    public void testMissingCompletionEvidence()
            throws Exception {

        int actionId = createAction();

        // I simulate a badly recorded completed action.
        // This bypasses the service only to test
        // the verification repository's protection.
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                    conn.prepareStatement(
                    "UPDATE grc_remediation_actions " +
                    "SET status = 'Completed', " +
                    "completion_evidence = NULL " +
                    "WHERE action_id = ?")) {

            stmt.setInt(1, actionId);
            stmt.executeUpdate();
        }

        String result = verify(
                REVIEWER_ID,
                "Verified"
        );

        assertTrue(result.contains(
                "All remediation actions must be completed"));

        assertEquals(0, verificationCount());
    }

    // ========================================================
    // 8. PREVENTING FINDING OWNER VERIFICATION
    // ========================================================

    public void testFindingOwnerCannotVerify()
            throws Exception {

        int actionId = createAction();

        completeAction(actionId);

        // Employee 101 owns this finding.
        String result = verify(
                OWNER_ID,
                "Verified"
        );

        assertTrue(result.contains(
                "Finding owner cannot verify"));

        assertEquals(0, verificationCount());
    }

    // ========================================================
    // 9. PREVENTING REMEDIATION SELF-VERIFICATION
    // ========================================================

    public void testRemediationAssigneeCannotVerify()
            throws Exception {

        int actionId = createAction();

        completeAction(actionId);

        // Employee 102 performed the corrective action.
        String result = verify(
                ASSIGNEE_ID,
                "Verified"
        );

        assertTrue(result.contains(
                "Remediation assignee cannot"));

        assertEquals(0, verificationCount());
    }

    // ========================================================
    // 10. REJECTING AN UNREGISTERED REVIEWER
    // ========================================================

    public void testUnregisteredReviewer()
            throws Exception {

        int actionId = createAction();

        completeAction(actionId);

        String result = verify(
                99999,
                "Verified"
        );

        assertTrue(result.contains(
                "Reviewer is not registered"));

        assertEquals(0, verificationCount());
    }

    // ========================================================
    // 11. PRESERVING REJECTED VERIFICATION HISTORY
    // ========================================================

    public void testRejectedThenVerified()
            throws Exception {

        int actionId = createAction();

        // I first reject the remediation.
        String rejectedResult = verify(
                REVIEWER_ID,
                "Rejected"
        );

        assertTrue(rejectedResult.contains(
                "Decision: Rejected"));

        // The corrective work is then reported completed.
        completeAction(actionId);

        // I independently verify the completed action.
        String verifiedResult = verify(
                REVIEWER_ID,
                "Verified"
        );

        assertTrue(verifiedResult.contains(
                "Decision: Verified"));

        // I confirm both decisions remain in history.
        List<RemediationVerification> history =
                verificationService.getVerificationHistory(
                        FINDING_ID);

        assertEquals(2, history.size());

        assertEquals(
                "Rejected",
                history.get(0).getDecision());

        assertEquals(
                "Verified",
                history.get(1).getDecision());
    }

    // ========================================================
    // 12. PREVENTING DUPLICATE SUCCESSFUL VERIFICATION
    // ========================================================

    public void testDuplicateVerifiedDecisionIsRejected()
            throws Exception {

        int actionId = createAction();

        completeAction(actionId);

        String firstResult = verify(
                REVIEWER_ID,
                "Verified"
        );

        assertTrue(firstResult.contains(
                "Decision: Verified"));

        // I attempt another verification after success.
        String secondResult = verify(
                REVIEWER_ID,
                "Verified"
        );

        assertTrue(secondResult.contains(
                "already has a successful verification"));

        assertEquals(1, verificationCount());
    }

    // ========================================================
    // 13. REQUIRING WRITTEN VERIFICATION NOTES
    // ========================================================

    public void testMissingVerificationNotes()
            throws Exception {

        int actionId = createAction();

        completeAction(actionId);

        String result =
                verificationService.verifyFinding(
                        FINDING_ID,
                        REVIEWER_ID,
                        "Verified",
                        "Simulated evidence was reviewed",
                        ""
                );

        assertTrue(result.contains(
                "Verification notes are required"));

        assertEquals(0, verificationCount());
    }

    // ========================================================
    // 14. INSERTING TEST EMPLOYEES
    // ========================================================

    private void insertEmployee(
            PreparedStatement stmt,
            int employeeId,
            String name,
            String role)
            throws Exception {

        // I reuse the prepared statement
        // to create separate employee records.
        stmt.setInt(1, employeeId);
        stmt.setString(2, name);
        stmt.setString(3, role);

        stmt.executeUpdate();
    }

    // ========================================================
    // 15. CREATING A REMEDIATION ACTION
    // ========================================================

    private int createAction() throws Exception {

        // I assign corrective work to employee 102.
        String result = actionService.registerAction(
                FINDING_ID,
                "Strengthen customer identity verification",
                ASSIGNEE_ID,
                futureDate()
        );

        assertTrue(result.contains(
                "Remediation action registered successfully"));

        List<RemediationAction> actions =
                actionService.getActionsByFinding(
                        FINDING_ID);

        assertEquals(1, actions.size());

        return actions.get(0).getActionId();
    }

    // ========================================================
    // 16. COMPLETING CORRECTIVE WORK
    // ========================================================

    private void completeAction(int actionId) {

        // I report that the assigned employee
        // has completed the corrective action.
        String result = actionService.completeAction(
                actionId,
                ASSIGNEE_ID,
                "RECOVERY-FIX-001"
        );

        assertTrue(result.contains(
                "Remediation action marked as completed"));
    }

    // ========================================================
    // 17. RECORDING A VERIFICATION DECISION
    // ========================================================

    private String verify(
            int reviewerId,
            String decision) {

        // I use one helper for all verification tests.
        return verificationService.verifyFinding(
                FINDING_ID,
                reviewerId,
                decision,
                "Simulated remediation evidence assessed",
                "Verification decision recorded for testing"
        );
    }

    // ========================================================
    // 18. COUNTING SAVED VERIFICATIONS
    // ========================================================

    private int verificationCount() throws Exception {

        return verificationService
                .getVerificationHistory(FINDING_ID)
                .size();
    }

    // ========================================================
    // 19. CHECKING THE FINDING STATUS
    // ========================================================

    private String getFindingStatus() throws Exception {

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                    conn.prepareStatement(
                    "SELECT status FROM grc_findings " +
                    "WHERE finding_id = ?")) {

            stmt.setInt(1, FINDING_ID);

            try (java.sql.ResultSet results =
                    stmt.executeQuery()) {

                assertTrue(results.next());

                return results.getString("status");
            }
        }
    }

    // ========================================================
    // 20. PREPARING A FUTURE DEADLINE
    // ========================================================

    private String futureDate() {

        return LocalDate.now()
                .plusDays(30)
                .toString();
    }
}