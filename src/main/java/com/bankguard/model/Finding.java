
package com.bankguard.model;

// ============================================================
// BANKGUARD v1.2 - FINDING MODEL
// ============================================================

// I use this class to represent a weakness
// identified during a control assessment.

public class Finding {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private final int findingId;
    private final int testId;

    private final String findingTitle;
    private final String findingDescription;
    private final String severity;

    private final int ownerId;

    private final String dueDate;
    private final String status;
    private final String createdAt;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    // I create a Finding object using information
    // retrieved from my SQLite database.

    public Finding(
            int findingId,
            int testId,
            String findingTitle,
            String findingDescription,
            String severity,
            int ownerId,
            String dueDate,
            String status,
            String createdAt) {

        this.findingId = findingId;
        this.testId = testId;
        this.findingTitle = findingTitle;
        this.findingDescription = findingDescription;
        this.severity = severity;
        this.ownerId = ownerId;
        this.dueDate = dueDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    // ========================================================
    // 3. ACCESSING MY FINDING INFORMATION
    // ========================================================

    public int getFindingId() {
        return findingId;
    }

    public int getTestId() {
        return testId;
    }

    public String getFindingTitle() {
        return findingTitle;
    }

    public String getFindingDescription() {
        return findingDescription;
    }

    public String getSeverity() {
        return severity;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}

