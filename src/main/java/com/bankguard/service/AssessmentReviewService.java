
package com.bankguard.service;

// ============================================================
// BANKGUARD - ASSESSMENT REVIEW SERVICE
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

import com.bankguard.model.AssessmentReview;
import com.bankguard.repository.AssessmentReviewRepository;

import java.io.IOException;
import java.sql.SQLException;

public class AssessmentReviewService {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private final AssessmentReviewRepository reviewRepository;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public AssessmentReviewService() {

        reviewRepository = new AssessmentReviewRepository();
    }

    // ========================================================
    // 3. REVIEWING A CONTROL ASSESSMENT
    // ========================================================

    public String reviewAssessment(
            int testId,
            int reviewerId,
            String decision,
            String evidenceAssessment,
            String reviewNotes) {

        // ========== DECLARATIONS ==========

        String validDecision;
        int rowsInserted;

        // ========== INITIALISATION ==========

        validDecision = null;
        rowsInserted = 0;

        // ========== VALIDATION ==========

        // I require a valid control assessment ID.
        if (testId <= 0) {

            return "Invalid assessment ID.";
        }

        // I require a valid reviewer employee ID.
        if (reviewerId <= 0) {

            return "Invalid reviewer ID.";
        }

        // I only allow supported decisions.
        if (decision != null) {

            switch (decision.trim().toLowerCase()) {

                case "approved" ->
                    validDecision = "Approved";

                case "rejected" ->
                    validDecision = "Rejected";

                default ->
                    validDecision = null;
            }
        }

        if (validDecision == null) {

            return "Invalid decision. Choose Approved or Rejected.";
        }

        // I require a written assessment of the evidence.
        if (evidenceAssessment == null
                || evidenceAssessment.isBlank()) {

            return "Evidence assessment is required.";
        }

        // I require reasons for the decision.
        if (reviewNotes == null || reviewNotes.isBlank()) {

            return "Review notes are required.";
        }

        // I limit my written responses.
        if (evidenceAssessment.trim().length() > 3000) {

            return "Evidence assessment is too long.";
        }

        if (reviewNotes.trim().length() > 3000) {

            return "Review notes are too long.";
        }

        // ========== PROCESSING ==========

        try {

            // I save the review after the repository
            // checks the tester, reviewer and evidence.
            rowsInserted = reviewRepository.saveReview(
                    testId,
                    reviewerId,
                    validDecision,
                    evidenceAssessment.trim(),
                    reviewNotes.trim()
            );

            // ========== OUTPUT ==========

            if (rowsInserted > 0) {

                return "Assessment review recorded successfully! "
                        + "Decision: " + validDecision;
            }

            return "Assessment review was not saved.";

        } catch (IllegalArgumentException e) {

            // I explain why the review was rejected.
            return "Review rejected: " + e.getMessage();

        } catch (SQLException | IOException e) {

            return "Assessment review failed: " + e.getMessage();
        }
    }

    // ========================================================
    // 4. VIEWING AN ASSESSMENT REVIEW
    // ========================================================

    public AssessmentReview getReview(int testId)
            throws SQLException, IOException {

        if (testId <= 0) {

            throw new IllegalArgumentException(
                    "Assessment ID must be greater than zero.");
        }

        return reviewRepository.findReviewByTest(testId);
    }
}

