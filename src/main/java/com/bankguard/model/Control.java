
package com.bankguard.model;

// ============================================================
// BANKGUARD v1.1 - CONTROL MODEL
// ============================================================

// I use this class to represent an IT or security control.
// Each control is linked to a risk in my bank's risk register.

public class Control {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private final int controlId;
    private final int riskId;
    private final String controlName;
    private final String controlObjective;
    private final String controlType;
    private final int ownerId;
    private final String frequency;
    private final String status;
    private final String createdAt;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    // I create a Control object using information
    // retrieved from my SQLite database.

    public Control(
            int controlId,
            int riskId,
            String controlName,
            String controlObjective,
            String controlType,
            int ownerId,
            String frequency,
            String status,
            String createdAt) {

        this.controlId = controlId;
        this.riskId = riskId;
        this.controlName = controlName;
        this.controlObjective = controlObjective;
        this.controlType = controlType;
        this.ownerId = ownerId;
        this.frequency = frequency;
        this.status = status;
        this.createdAt = createdAt;
    }

    // ========================================================
    // 3. ACCESSING MY CONTROL INFORMATION
    // ========================================================

    // I use getters to read control information.
    // I do not directly change these saved values.

    public int getControlId() {
        return controlId;
    }

    public int getRiskId() {
        return riskId;
    }

    public String getControlName() {
        return controlName;
    }

    public String getControlObjective() {
        return controlObjective;
    }

    public String getControlType() {
        return controlType;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}

