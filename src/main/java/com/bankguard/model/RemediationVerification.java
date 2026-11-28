
package com.bankguard.model;

// ============================================================
// BANKGUARD v1.2 - REMEDIATION VERIFICATION MODEL
// ============================================================

// I use this class to represent an independent review
// of corrective work linked to a finding.

public class RemediationVerification {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private final int verificationId;
    private final int findingId;
    private final int reviewerId;

    private final String decision;
    private final String evidenceAssessment;
    private final String verificationNotes;
    private final String verifiedAt;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public RemediationVerification(
            int verificationId,
            int findingId,
            int reviewerId,
            String decision,
            String evidenceAssessment,
            String verificationNotes,
            String verifiedAt) {

        this.verificationId = verificationId;
        this.findingId = findingId;
        this.reviewerId = reviewerId;
        this.decision = decision;
        this.evidenceAssessment = evidenceAssessment;
        this.verificationNotes = verificationNotes;
        this.verifiedAt = verifiedAt;
    }

    // ========================================================
    // 3. ACCESSING MY VERIFICATION INFORMATION
    // ========================================================

    public int getVerificationId() {
        return verificationId;
    }

    public int getFindingId() {
        return findingId;
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

    public String getVerificationNotes() {
        return verificationNotes;
    }

    public String getVerifiedAt() {
        return verifiedAt;
    }
}