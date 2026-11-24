
package com.bankguard.model;

public class Risk {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    // I store the information that describes a risk.
    private int riskId;
    private String title;
    private String description;
    private int likelihood;
    private int impact;
    private int riskScore;
    private String ownerName;
    private String status;

    // ========================================================
    // 2. INITIALISING MY RISK
    // ========================================================

    // I use this constructor to create a Risk object
    // from information retrieved from SQLite.
    public Risk(
            int riskId,
            String title,
            String description,
            int likelihood,
            int impact,
            int riskScore,
            String ownerName,
            String status) {

        // I give my risk object its information.
        this.riskId = riskId;
        this.title = title;
        this.description = description;
        this.likelihood = likelihood;
        this.impact = impact;
        this.riskScore = riskScore;
        this.ownerName = ownerName;
        this.status = status;
    }

    // ========================================================
    // 3. GETTING MY RISK INFORMATION
    // ========================================================

    // I use these methods when I need to read
    // information stored inside my Risk object.

    public int getRiskId() {
        return riskId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getLikelihood() {
        return likelihood;
    }

    public int getImpact() {
        return impact;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getStatus() {
        return status;
    }
}