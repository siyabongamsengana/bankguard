
package com.bankguard;

// ============================================================
// BANKGUARD - ASSESSMENT REVIEW AUTOMATED TESTS
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

// I use JUnit to test my assessment review rules.
import junit.framework.TestCase;

// I use my real BankGuard services.
import com.bankguard.database.DatabaseManager;
import com.bankguard.model.AssessmentReview;
import com.bankguard.service.ControlService;
import com.bankguard.service.ControlTestService;
import com.bankguard.service.EvidenceService;
import com.bankguard.service.AssessmentReviewService;

// I use Java and SQLite to prepare test records.
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AssessmentReviewTest extends TestCase {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private static final String DATABASE_PROPERTY =
            "bankguard.db.url";

    private Path testDatabase;
    private String previousDatabaseUrl;

    private ControlService controlService;
    private ControlTestService controlTestService;
    private EvidenceService evidenceService;
    private AssessmentReviewService reviewService;

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
                "bankguard-review-test-", ".db");

        // I use this database instead of bankguard.db.
        System.setProperty(
                DATABASE_PROPERTY,
                "jdbc:sqlite:" + testDatabase.toAbsolutePath()
        );

        // I create my tables, including assessment reviews.
        DatabaseManager manager = new DatabaseManager();

        assertTrue(
                "Test database setup failed.",
                manager.createTables()
        );

        // I prepare two employees:
        // 101 performs the assessment.
        // 102 independently reviews the assessment.
        try (Connection conn = DatabaseManager.connect()) {

            try (PreparedStatement stmt =
                    conn.prepareStatement(
                    "INSERT INTO users " +
                    "(user_id, name, role) VALUES (?, ?, ?)")) {

                stmt.setInt(1, 101);
                stmt.setString(2, "Test Analyst");
                stmt.setString(3, "GRC Analyst");
                stmt.executeUpdate();

                stmt.setInt(1, 102);
                stmt.setString(2, "Test Reviewer");
                stmt.setString(3, "GRC Manager");
                stmt.executeUpdate();
            }

            // I prepare a fictional banking IT risk.
            try (PreparedStatement stmt =
                    conn.prepareStatement(
                    "INSERT INTO risks " +
                    "(risk_id, title, description, " +
                    "likelihood, impact, owner_id) " +
                    "VALUES (101, ?, ?, 3, 5, 101)")) {

                stmt.setString(1, "Account Takeover Test Risk");
                stmt.setString(2,
                        "Simulated account takeover risk");
                stmt.executeUpdate();
            }
        }

        // I prepare the services being tested.
        controlService = new ControlService();
        controlTestService = new ControlTestService();
        evidenceService = new EvidenceService();
        reviewService = new AssessmentReviewService();
    }

    // ========================================================
    // 3. CLEANING UP AFTER EACH TEST
    // ========================================================

    @Override
    protected void tearDown() throws Exception {

        try {

            // I restore the original database property.
            if (previousDatabaseUrl == null) {

                System.clearProperty(DATABASE_PROPERTY);

            } else {

                System.setProperty(
                        DATABASE_PROPERTY,
                        previousDatabaseUrl
                );
            }

            // I delete only the temporary test files.
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
    // 4. APPROVING AN ASSESSMENT WITH EVIDENCE
    // ========================================================

    public void testApprovalWithEvidence() throws Exception {

        int testId = createAssessment(true);

        String result = reviewService.reviewAssessment(
                testId,
                102,
                "Approved",
                "Simulated MFA records are relevant " +
                        "and sufficient for this exercise.",
                "The documented assessment is supported."
        );

        assertTrue(result.contains(
                "Assessment review recorded successfully"));

        AssessmentReview savedReview =
                reviewService.getReview(testId);

        assertNotNull(savedReview);
        assertEquals("Approved", savedReview.getDecision());
        assertEquals(102, savedReview.getReviewerId());
    }

    // ========================================================
    // 5. REJECTING APPROVAL WITHOUT EVIDENCE
    // ========================================================

    public void testApprovalWithoutEvidenceIsRejected()
            throws Exception {

        int testId = createAssessment(false);

        String result = reviewService.reviewAssessment(
                testId,
                102,
                "Approved",
                "No evidence has been registered.",
                "The assessment should not be approved."
        );

        assertTrue(result.contains(
                "Approval requires evidence"));

        assertNull(reviewService.getReview(testId));
    }

    // ========================================================
    // 6. PREVENTING SELF-REVIEW
    // ========================================================

    public void testSelfReviewIsRejected() throws Exception {

        int testId = createAssessment(true);

        // Employee 101 performed the original test.
        // The same employee must not review it.
        String result = reviewService.reviewAssessment(
                testId,
                101,
                "Approved",
                "Simulated evidence reviewed.",
                "Attempting to review my own work."
        );

        assertTrue(result.contains(
                "Self-review is not allowed"));

        assertNull(reviewService.getReview(testId));
    }

    // ========================================================
    // 7. REJECTING AN UNREGISTERED REVIEWER
    // ========================================================

    public void testUnregisteredReviewerIsRejected()
            throws Exception {

        int testId = createAssessment(true);

        String result = reviewService.reviewAssessment(
                testId,
                99999,
                "Approved",
                "Simulated evidence reviewed.",
                "Reviewer does not exist."
        );

        assertTrue(result.contains(
                "Reviewer is not registered"));

        assertNull(reviewService.getReview(testId));
    }

    // ========================================================
    // 8. ALLOWING REJECTION WITHOUT EVIDENCE
    // ========================================================

    public void testRejectionWithoutEvidence()
            throws Exception {

        int testId = createAssessment(false);

        String result = reviewService.reviewAssessment(
                testId,
                102,
                "Rejected",
                "No supporting evidence was provided.",
                "The analyst must supply evidence."
        );

        assertTrue(result.contains(
                "Assessment review recorded successfully"));

        AssessmentReview savedReview =
                reviewService.getReview(testId);

        assertNotNull(savedReview);
        assertEquals("Rejected", savedReview.getDecision());
    }

    // ========================================================
    // 9. PREVENTING DUPLICATE REVIEW DECISIONS
    // ========================================================

    public void testDuplicateReviewIsRejected()
            throws Exception {

        int testId = createAssessment(true);

        String firstResult = reviewService.reviewAssessment(
                testId,
                102,
                "Approved",
                "Evidence is sufficient for the simulation.",
                "Initial review decision."
        );

        assertTrue(firstResult.contains(
                "Assessment review recorded successfully"));

        String secondResult = reviewService.reviewAssessment(
                testId,
                102,
                "Rejected",
                "Attempt to change the decision.",
                "Second review attempt."
        );

        assertTrue(secondResult.contains(
                "Assessment already reviewed"));

        // I verify the original decision remains saved.
        assertEquals(
                "Approved",
                reviewService.getReview(testId).getDecision()
        );
    }

    // ========================================================
    // 10. REQUIRING WRITTEN REVIEW REASONS
    // ========================================================

    public void testMissingReviewNotesIsRejected()
            throws Exception {

        int testId = createAssessment(true);

        String result = reviewService.reviewAssessment(
                testId,
                102,
                "Approved",
                "Evidence was evaluated.",
                ""
        );

        assertTrue(result.contains(
                "Review notes are required"));

        assertNull(reviewService.getReview(testId));
    }

    // ========================================================
    // 11. REJECTING INVALID DECISIONS
    // ========================================================

    public void testInvalidReviewDecision()
            throws Exception {

        int testId = createAssessment(true);

        String result = reviewService.reviewAssessment(
                testId,
                102,
                "Automatically Approved",
                "Evidence was evaluated.",
                "Invalid decision test."
        );

        assertTrue(result.contains("Invalid decision"));

        assertNull(reviewService.getReview(testId));
    }

    // ========================================================
    // 12. CREATING A CONTROL ASSESSMENT FOR TESTING
    // ========================================================

    private int createAssessment(boolean includeEvidence)
            throws Exception {

        // I register a security control for my risk.
        String controlResult = controlService.registerControl(
                101,
                "Multi-Factor Authentication",
                "Prevent unauthorised customer access",
                "Preventive",
                101,
                "Every login"
        );

        assertTrue(controlResult.contains(
                "Control registered successfully"));

        int controlId = getLatestId(
                "grc_controls", "control_id");

        // I record a simulated assessment.
        String testResult =
                controlTestService.registerControlTest(
                        controlId,
                        "Review simulated MFA configuration",
                        "November 2026",
                        "Evidence review is pending",
                        "Not Tested",
                        101
                );

        assertTrue(testResult.contains(
                "Control test recorded successfully"));

        int testId = getLatestId(
                "grc_control_tests", "test_id");

        // I register evidence only when the specific
        // automated test requires supporting evidence.
        if (includeEvidence) {

            String evidenceResult =
                    evidenceService.registerEvidence(
                            testId,
                            "Simulated MFA Report",
                            "Fictional Security Team",
                            "Simulated MFA configuration records",
                            "MFA-TEST-001"
                    );

            assertTrue(evidenceResult.contains(
                    "Evidence reference registered successfully"));
        }

        return testId;
    }

    // ========================================================
    // 13. FINDING THE NEWEST TEST RECORD ID
    // ========================================================

    private int getLatestId(
            String tableName,
            String columnName) throws Exception {

        // I use this helper only with fixed table names
        // supplied by my own automated tests.
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