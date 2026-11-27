
package com.bankguard.model;

// ============================================================
// BANKGUARD - ASSESSMENT REVIEW MODEL
// ============================================================

public class AssessmentReview {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private final int reviewId;
    private final int testId;
    private final int reviewerId;
    private final String decision;
    private final String evidenceAssessment;
    private final String reviewNotes;
    private final String reviewedAt;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public AssessmentReview(
            int reviewId,
            int testId,
            int reviewerId,
            String decision,
            String evidenceAssessment,
            String reviewNotes,
            String reviewedAt) {

        this.reviewId = reviewId;
        this.testId = testId;
        this.reviewerId = reviewerId;
        this.decision = decision;
        this.evidenceAssessment = evidenceAssessment;
        this.reviewNotes = reviewNotes;
        this.reviewedAt = reviewedAt;
    }

    // ========================================================
    // 3. GETTING MY REVIEW INFORMATION
    // ========================================================

    public int getReviewId() {
        return reviewId;
    }

    public int getTestId() {
        return testId;
    }

    public int getReviewerId() {
        return reviewerId;
    }

    public String getDecision() {
        return decision;
    }

    public String getEvidenceAssessment() {
        return evidenceAssessment;
    }

    public String getReviewNotes() {
        return reviewNotes;
    }

    public String getReviewedAt() {
        return reviewedAt;
    }
}
