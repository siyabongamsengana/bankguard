
package com.bankguard.model;

// ============================================================
// BANKGUARD v1.0 - PROJECT TASK MODEL
// ============================================================

// I use this class to represent one task that belongs
// to a GRC project.

// For example, a PCI DSS Gap Analysis project may have
// tasks for defining scope, reviewing evidence and
// documenting compliance gaps.

public class ProjectTask {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private final int taskId;
    private final int projectId;
    private final String taskName;
    private final String description;
    private final String status;
    private final String createdAt;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    // I create a task object using the information
    // retrieved from my SQLite database.

    public ProjectTask(
            int taskId,
            int projectId,
            String taskName,
            String description,
            String status,
            String createdAt) {

        this.taskId = taskId;
        this.projectId = projectId;
        this.taskName = taskName;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    // ========================================================
    // 3. ACCESSING TASK INFORMATION
    // ========================================================

    // I use getters to read task information safely.

    public int getTaskId() {
        return taskId;
    }

    public int getProjectId() {
        return projectId;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}

