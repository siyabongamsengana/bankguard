
package com.bankguard.model;

// ============================================================
// BANKGUARD v1.1 - EVIDENCE MODEL
// ============================================================

// I use this class to record evidence references
// supporting my control testing assessments.

// This version stores information about evidence.
// It does not yet upload or store the actual files.

public class Evidence {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private final int evidenceId;
    private final int testId;
    private final String evidenceName;
    private final String evidenceSource;
    private final String evidenceDescription;
    private final String evidenceReference;
    private final String recordedAt;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    // I create an Evidence object using information
    // retrieved from my SQLite database.

    public Evidence(
            int evidenceId,
            int testId,
            String evidenceName,
            String evidenceSource,
            String evidenceDescription,
            String evidenceReference,
            String recordedAt) {

        this.evidenceId = evidenceId;
        this.testId = testId;
        this.evidenceName = evidenceName;
        this.evidenceSource = evidenceSource;
        this.evidenceDescription = evidenceDescription;
        this.evidenceReference = evidenceReference;
        this.recordedAt = recordedAt;
    }

    // ========================================================
    // 3. ACCESSING MY EVIDENCE INFORMATION
    // ========================================================

    // I use getters to retrieve information
    // about evidence linked to a control test.

    public int getEvidenceId() {
        return evidenceId;
    }

    public int getTestId() {
        return testId;
    }

    public String getEvidenceName() {
        return evidenceName;
    }

    public String getEvidenceSource() {
        return evidenceSource;
    }

    public String getEvidenceDescription() {
        return evidenceDescription;
    }

    public String getEvidenceReference() {
        return evidenceReference;
    }

    public String getRecordedAt() {
        return recordedAt;
    }
}
