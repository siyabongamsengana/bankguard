
package com.bankguard.model;

// ============================================================
// BANKGUARD v0.9 - AUDIT LOG MODEL
// ============================================================

// I use this class to represent one recorded risk change.

public class AuditLog {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private final int auditId;
    private final int riskId;
    private final String action;
    private final String oldValue;
    private final String newValue;
    private final int changedBy;
    private final String changedAt;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    // I use this constructor when reading an audit record
    // that has already been saved in my database.

    public AuditLog(
            int auditId,
            int riskId,
            String action,
            String oldValue,
            String newValue,
            int changedBy,
            String changedAt) {

        this.auditId = auditId;
        this.riskId = riskId;
        this.action = action;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }

    // ========================================================
    // 3. ACCESSING MY AUDIT INFORMATION
    // ========================================================

    // I use getters to read information without
    // allowing other classes to change a saved record.

    public int getAuditId() {
        return auditId;
    }

    public int getRiskId() {
        return riskId;
    }

    public String getAction() {
        return action;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public int getChangedBy() {
        return changedBy;
    }

    public String getChangedAt() {
        return changedAt;
    }
}
