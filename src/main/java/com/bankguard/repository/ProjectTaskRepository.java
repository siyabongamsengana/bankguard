
package com.bankguard.repository;

// ============================================================
// IMPORTS
// ============================================================

// I import my database classes and project task model.
// I use JDBC to manage my assessment tasks.

import com.bankguard.database.DatabaseManager;
import com.bankguard.database.SqlLoader;
import com.bankguard.model.ProjectTask;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProjectTaskRepository {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private static final String CREATE_TASK_FILE =
            "/sql/tasks/create_task.sql";

    private static final String VIEW_TASKS_FILE =
            "/sql/tasks/view_project_tasks.sql";

    private static final String UPDATE_TASK_FILE =
            "/sql/tasks/update_task_status.sql";

    private static final String PROGRESS_FILE =
            "/sql/tasks/calculate_project_progress.sql";

    // ========================================================
    // 2. CREATING A PROJECT TASK
    // ========================================================

    public int createTask(
            int projectId,
            String taskName,
            String description)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        int rowsInserted;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(CREATE_TASK_FILE);
        rowsInserted = 0;

        // ========== PROCESSING ==========

        // I save a task linked to an existing GRC project.
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, projectId);
            stmt.setString(2, taskName);
            stmt.setString(3, description);

            rowsInserted = stmt.executeUpdate();
        }

        // ========== OUTPUT ==========

        return rowsInserted;
    }

    // ========================================================
    // 3. VIEWING TASKS FOR ONE PROJECT
    // ========================================================

    public List<ProjectTask> findTasksByProject(int projectId)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        List<ProjectTask> tasks;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(VIEW_TASKS_FILE);
        tasks = new ArrayList<>();

        // ========== PROCESSING ==========

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            // I select the project whose tasks I want.
            stmt.setInt(1, projectId);

            try (ResultSet results = stmt.executeQuery()) {

                while (results.next()) {

                    // I convert each row into a task object.
                    ProjectTask task = new ProjectTask(
                            results.getInt("task_id"),
                            results.getInt("project_id"),
                            results.getString("task_name"),
                            results.getString("description"),
                            results.getString("status"),
                            results.getString("created_at")
                    );

                    tasks.add(task);
                }
            }
        }

        // ========== OUTPUT ==========

        return tasks;
    }

    // ========================================================
    // 4. UPDATING TASK STATUS
    // ========================================================

    public int updateTaskStatus(
            int projectId,
            int taskId,
            String newStatus)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        int rowsUpdated;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(UPDATE_TASK_FILE);
        rowsUpdated = 0;

        // ========== PROCESSING ==========

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            // I supply the new task status.
            stmt.setString(1, newStatus);

            // I identify the task I want to change.
            stmt.setInt(2, taskId);

            // I make sure the task belongs
            // to the selected project.
            stmt.setInt(3, projectId);

            rowsUpdated = stmt.executeUpdate();
        }

        // ========== OUTPUT ==========

        return rowsUpdated;
    }

    // ========================================================
    // 5. CALCULATING PROJECT PROGRESS
    // ========================================================

    public int calculateProgress(int projectId)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        int totalTasks;
        int completedTasks;
        int progress;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(PROGRESS_FILE);
        totalTasks = 0;
        completedTasks = 0;
        progress = 0;

        // ========== PROCESSING ==========

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, projectId);

            try (ResultSet results = stmt.executeQuery()) {

                if (results.next()) {

                    // I retrieve the number of tasks.
                    totalTasks =
                            results.getInt("total_tasks");

                    // I retrieve the number completed.
                    completedTasks =
                            results.getInt("completed_tasks");
                }
            }
        }

        // I avoid division by zero when
        // a project has no tasks yet.
        if (totalTasks > 0) {

            // I calculate the percentage completed.
            progress = (int) Math.round(
                    completedTasks * 100.0 / totalTasks
            );
        }

        // ========== OUTPUT ==========

        return progress;
    }
}
