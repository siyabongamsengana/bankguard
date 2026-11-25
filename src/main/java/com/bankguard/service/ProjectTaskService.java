
package com.bankguard.service;

// ============================================================
// BANKGUARD v1.0 - PROJECT TASK SERVICE
// ============================================================

// I use this service to manage the tasks inside
// my practical GRC projects.

import com.bankguard.model.ProjectTask;
import com.bankguard.repository.GrcProjectRepository;
import com.bankguard.repository.ProjectTaskRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ProjectTaskService {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    // I use this repository to work with my tasks.
    private final ProjectTaskRepository taskRepository;

    // I use this repository to verify project IDs.
    private final GrcProjectRepository projectRepository;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public ProjectTaskService() {

        // I initialise the repositories I need.
        taskRepository = new ProjectTaskRepository();
        projectRepository = new GrcProjectRepository();
    }

    // ========================================================
    // 3. CREATING A NEW PROJECT TASK
    // ========================================================

    public String createTask(
            int projectId,
            String taskName,
            String description) {

        // ========== VALIDATION ==========

        // I require a valid project ID.
        if (projectId <= 0) {

            return "Invalid project ID.";
        }

        // I require a task name.
        if (taskName == null || taskName.isBlank()) {

            return "Task name is required.";
        }

        // I limit the task name length.
        if (taskName.trim().length() > 150) {

            return "Task name cannot exceed 150 characters.";
        }

        // I allow a task description to be empty,
        // but I do not accept descriptions that are too long.
        if (description != null
                && description.trim().length() > 2000) {

            return "Task description cannot exceed "
                    + "2000 characters.";
        }

        // ========== PROCESSING ==========

        try {

            // I make sure the project exists before
            // assigning a task to it.
            if (!projectRepository.projectExists(projectId)) {

                return "Project not found. "
                        + "Create the project first.";
            }

            // I use an empty description if none was given.
            String cleanDescription =
                    description == null ? "" : description.trim();

            // I save my task under the selected project.
            int rowsInserted = taskRepository.createTask(
                    projectId,
                    taskName.trim(),
                    cleanDescription
            );

            // ========== OUTPUT ==========

            if (rowsInserted > 0) {

                return "Project task created successfully!";
            }

            return "Project task was not created.";

        } catch (SQLException | IOException e) {

            return "Task creation failed: " + e.getMessage();
        }
    }

    // ========================================================
    // 4. VIEWING TASKS FOR A GRC PROJECT
    // ========================================================

    public List<ProjectTask> getProjectTasks(int projectId)
            throws SQLException, IOException {

        // ========== VALIDATION ==========

        if (projectId <= 0) {

            throw new IllegalArgumentException(
                    "Project ID must be greater than zero.");
        }

        // ========== OUTPUT ==========

        return taskRepository.findTasksByProject(projectId);
    }

    // ========================================================
    // 5. UPDATING THE STATUS OF A PROJECT TASK
    // ========================================================

    public String changeTaskStatus(
            int projectId,
            int taskId,
            String newStatus) {

        // ========== DECLARATIONS ==========

        String validStatus;
        int rowsUpdated;

        // ========== INITIALISATION ==========

        validStatus = null;
        rowsUpdated = 0;

        // ========== VALIDATION ==========

        // I need valid IDs for the project and task.
        if (projectId <= 0 || taskId <= 0) {

            return "Invalid project or task ID.";
        }

        // I only accept the task statuses
        // supported by my database.
        if (newStatus != null) {

            switch (newStatus.trim().toLowerCase()) {

                case "not started" ->
                    validStatus = "Not Started";

                case "in progress" ->
                    validStatus = "In Progress";

                case "completed" ->
                    validStatus = "Completed";

                default ->
                    validStatus = null;
            }
        }

        if (validStatus == null) {

            return "Invalid task status. "
                    + "Choose Not Started, In Progress "
                    + "or Completed.";
        }

        // ========== PROCESSING ==========

        try {

            // I check whether the selected project exists.
            if (!projectRepository.projectExists(projectId)) {

                return "Project not found.";
            }

            // I update only a task linked
            // to the selected project.
            rowsUpdated = taskRepository.updateTaskStatus(
                    projectId,
                    taskId,
                    validStatus
            );

            // ========== OUTPUT ==========

            if (rowsUpdated > 0) {

                return "Task status updated to: "
                        + validStatus;
            }

            return "Task not found in the selected project.";

        } catch (SQLException | IOException e) {

            return "Task status update failed: "
                    + e.getMessage();
        }
    }

    // ========================================================
    // 6. CALCULATING MY PROJECT PROGRESS
    // ========================================================

    public int getProjectProgress(int projectId)
            throws SQLException, IOException {

        // ========== VALIDATION ==========

        if (projectId <= 0) {

            throw new IllegalArgumentException(
                    "Project ID must be greater than zero.");
        }

        // I reject project IDs that do not exist.
        if (!projectRepository.projectExists(projectId)) {

            throw new IllegalArgumentException(
                    "Project not found.");
        }

        // ========== OUTPUT ==========

        // I calculate progress using completed tasks.
        return taskRepository.calculateProgress(projectId);
    }
}

