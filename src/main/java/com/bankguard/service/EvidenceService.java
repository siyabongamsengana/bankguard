
package com.bankguard.service;

// ============================================================
// BANKGUARD v1.1 - EVIDENCE SERVICE
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

// I use this service to manage evidence references
// supporting control assessments in BankGuard.

import com.bankguard.model.Evidence;
import com.bankguard.repository.EvidenceRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class EvidenceService {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    // I use my repository to save evidence records.
    private final EvidenceRepository evidenceRepository;

    // I use this service to verify control test IDs.
    private final ControlTestService testService;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public EvidenceService() {

        evidenceRepository = new EvidenceRepository();
        testService = new ControlTestService();
    }

    // ========================================================
    // 3. REGISTERING EVIDENCE
    // ========================================================

    public String registerEvidence(
            int testId,
            String evidenceName,
            String evidenceSource,
            String evidenceDescription,
            String evidenceReference) {

        // ========== DECLARATIONS ==========

        int rowsInserted;

        // ========== INITIALISATION ==========

        rowsInserted = 0;

        // ========== VALIDATION ==========

        // I require an existing control test.
        if (testId <= 0) {

            return "Invalid control test ID.";
        }

        // I require a meaningful evidence name.
        if (evidenceName == null
                || evidenceName.isBlank()) {

            return "Evidence name is required.";
        }

        // I record the source of the evidence.
        if (evidenceSource == null
                || evidenceSource.isBlank()) {

            return "Evidence source is required.";
        }

        // I explain what the evidence contains.
        if (evidenceDescription == null
                || evidenceDescription.isBlank()) {

            return "Evidence description is required.";
        }

        // I preserve a reference to the evidence.
        if (evidenceReference == null
                || evidenceReference.isBlank()) {

            return "Evidence reference is required.";
        }

        // I limit field lengths to manageable values.
        if (evidenceName.trim().length() > 150) {

            return "Evidence name cannot exceed "
                    + "150 characters.";
        }

        if (evidenceSource.trim().length() > 250) {

            return "Evidence source cannot exceed "
                    + "250 characters.";
        }

        if (evidenceDescription.trim().length() > 3000) {

            return "Evidence description cannot exceed "
                    + "3000 characters.";
        }

        if (evidenceReference.trim().length() > 500) {

            return "Evidence reference cannot exceed "
                    + "500 characters.";
        }

        // ========== PROCESSING ==========

        try {

            // I confirm that the selected test exists.
            if (!testService.testExists(testId)) {

                return "Control test not found. "
                        + "Register the test first.";
            }

            // I save the evidence information in SQLite.
            rowsInserted = evidenceRepository.registerEvidence(
                    testId,
                    evidenceName.trim(),
                    evidenceSource.trim(),
                    evidenceDescription.trim(),
                    evidenceReference.trim()
            );

            // ========== OUTPUT ==========

            if (rowsInserted > 0) {

                return "Evidence reference registered "
                        + "successfully!";
            }

            return "Evidence reference was not registered.";

        } catch (SQLException | IOException e) {

            return "Evidence registration failed: "
                    + e.getMessage();
        }
    }

    // ========================================================
    // 4. VIEWING EVIDENCE FOR A CONTROL TEST
    // ========================================================

    public List<Evidence> getEvidenceByTest(int testId)
            throws SQLException, IOException {

        // ========== VALIDATION ==========

        if (testId <= 0) {

            throw new IllegalArgumentException(
                    "Control test ID must be greater than zero.");
        }

        // ========== OUTPUT ==========

        return evidenceRepository.findEvidenceByTest(testId);
    }

    // ========================================================
    // 5. CHECKING WHETHER EVIDENCE EXISTS
    // ========================================================

    public boolean hasEvidence(int testId)
            throws SQLException, IOException {

        // ========== VALIDATION ==========

        if (testId <= 0) {

            return false;
        }

        // ========== PROCESSING ==========

        // I check for saved evidence references.
        // I do not assume that having a reference
        // proves the underlying evidence is reliable.
        List<Evidence> records =
                evidenceRepository.findEvidenceByTest(testId);

        // ========== OUTPUT ==========

        return !records.isEmpty();
    }
}
