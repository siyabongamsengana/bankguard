
package com.bankguard.model;

// ============================================================
// BANKGUARD v1.2 - REMEDIATION ACTION MODEL
// ============================================================

// I use this class to represent corrective work
// assigned to resolve an identified finding.

public class RemediationAction {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private final int actionId;
    private final int findingId;

    private final String actionDescription;

    private final int assignedTo;

    private final String dueDate;
    private final String status;
    private final String completionEvidence;
    private final String createdAt;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    // I create a RemediationAction object using
    // information retrieved from my SQLite database.

    public RemediationAction(
            int actionId,
            int findingId,
            String actionDescription,
            int assignedTo,
            String dueDate,
            String status,
            String completionEvidence,
            String createdAt) {

        this.actionId = actionId;
        this.findingId = findingId;
        this.actionDescription = actionDescription;
        this.assignedTo = assignedTo;
        this.dueDate = dueDate;
        this.status = status;
        this.completionEvidence = completionEvidence;
        this.createdAt = createdAt;
    }

    // ========================================================
    // 3. ACCESSING MY REMEDIATION INFORMATION
    // ========================================================

    public int getActionId() {
        return actionId;
    }

    public int getFindingId() {
        return findingId;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public int getAssignedTo() {
        return assignedTo;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getStatus() {
        return status;
    }

    public String getCompletionEvidence() {
        return completionEvidence;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}
