
package com.bankguard.service;

// ============================================================
// BANKGUARD v1.2 - REMEDIATION VERIFICATION SERVICE
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

// I import my verification model and repository.
// I use these to validate independent review decisions.

import com.bankguard.model.RemediationVerification;
import com.bankguard.repository.RemediationVerificationRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class RemediationVerificationService {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    // I use this repository to save independent
    // verification decisions and retrieve their history.
    private final RemediationVerificationRepository
            verificationRepository;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public RemediationVerificationService() {

        // I prepare the repository used to manage
        // independent remediation verification.
        verificationRepository =
                new RemediationVerificationRepository();
    }

    // ========================================================
    // 3. RECORDING AN INDEPENDENT VERIFICATION
    // ========================================================

    public String verifyFinding(
            int findingId,
            int reviewerId,
            String decision,
            String evidenceAssessment,
            String notes) {

        // ========== DECLARATIONS ==========

        String validDecision;
        int rowsInserted;

        // ========== INITIALISATION ==========

        validDecision = null;
        rowsInserted = 0;

        // ========== VALIDATION ==========

        // I require a valid finding ID.
        if (findingId <= 0) {

            return "Invalid finding ID.";
        }

        // I require a valid employee ID
        // for the independent reviewer.
        if (reviewerId <= 0) {

            return "Invalid reviewer ID.";
        }

        // I only accept the two verification
        // decisions supported by BankGuard.
        if (decision != null) {

            switch (decision.trim().toLowerCase()) {

                case "verified" ->
                    validDecision = "Verified";

                case "rejected" ->
                    validDecision = "Rejected";

                default ->
                    validDecision = null;
            }
        }

        if (validDecision == null) {

            return "Invalid decision. Choose Verified or Rejected.";
        }

        // I require a written evaluation
        // of the remediation evidence.
        if (evidenceAssessment == null
                || evidenceAssessment.isBlank()) {

            return "Evidence assessment is required.";
        }

        // I require an explanation of why
        // the reviewer made this decision.
        if (notes == null || notes.isBlank()) {

            return "Verification notes are required.";
        }

        // I limit the length of my written records.
        if (evidenceAssessment.trim().length() > 3000) {

            return "Evidence assessment is too long.";
        }

        if (notes.trim().length() > 3000) {

            return "Verification notes are too long.";
        }

        // ========== PROCESSING ==========

        try {

            // I ask the repository to check:
            // 1. The finding exists and is not closed.
            // 2. The reviewer is a registered employee.
            // 3. The reviewer is not the finding owner.
            // 4. The reviewer did not perform remediation.
            // 5. All actions are completed before approval.
            // 6. A successful verification is not duplicated.

            rowsInserted =
                    verificationRepository.registerVerification(
                            findingId,
                            reviewerId,
                            validDecision,
                            evidenceAssessment.trim(),
                            notes.trim()
                    );

            // ========== OUTPUT ==========

            if (rowsInserted == 1) {

                return "Remediation verification recorded "
                        + "successfully! Decision: "
                        + validDecision
                        + ". Finding closure is a separate step.";
            }

            return "Remediation verification was not saved.";

        } catch (IllegalArgumentException e) {

            // I explain why BankGuard rejected
            // the verification attempt.
            return "Verification rejected: "
                    + e.getMessage();

        } catch (SQLException | IOException e) {

            // I report database problems without
            // incorrectly recording a successful review.
            return "Remediation verification failed: "
                    + e.getMessage();
        }
    }

    // ========================================================
    // 4. VIEWING VERIFICATION HISTORY
    // ========================================================

    public List<RemediationVerification>
            getVerificationHistory(int findingId)
            throws SQLException, IOException {

        // ========== VALIDATION ==========

        // I require a valid finding ID.
        if (findingId <= 0) {

            throw new IllegalArgumentException(
                    "Finding ID must be greater than zero.");
        }

        // ========== PROCESSING ==========

        // I retrieve all recorded verification attempts.
        // This includes rejected and successful decisions.
        return verificationRepository
                .findVerificationsByFinding(findingId);
    }
}
