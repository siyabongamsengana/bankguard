
package com.bankguard.model;

// ============================================================
// BANKGUARD v1.0 - GRC PROJECT MODEL
// ============================================================

// I use this class to represent a GRC project.
// A project could be a PCI DSS gap analysis,
// IT risk assessment or control testing assignment.

public class GrcProject {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private final int projectId;
    private final String projectName;
    private final String description;
    private final String category;
    private final String status;
    private final String createdAt;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    // I create a project object using information
    // retrieved from my SQLite database.

    public GrcProject(
            int projectId,
            String projectName,
            String description,
            String category,
            String status,
            String createdAt) {

        this.projectId = projectId;
        this.projectName = projectName;
        this.description = description;
        this.category = category;
        this.status = status;
        this.createdAt = createdAt;
    }

    // ========================================================
    // 3. ACCESSING PROJECT INFORMATION
    // ========================================================

    // I use getters so other classes can read
    // project information without modifying it directly.

    public int getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}


