
package com.bankguard.repository;

// ============================================================
// IMPORTS
// ============================================================

// I import my database classes and GRC project model.
// I use JDBC to save and retrieve project information.

import com.bankguard.database.DatabaseManager;
import com.bankguard.database.SqlLoader;
import com.bankguard.model.GrcProject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GrcProjectRepository {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    // I keep all my project SQL file locations together.

    private static final String CREATE_PROJECT_FILE =
            "/sql/projects/create_project.sql";

    private static final String VIEW_PROJECTS_FILE =
            "/sql/projects/view_projects.sql";

    private static final String FIND_PROJECT_FILE =
            "/sql/projects/find_project.sql";

    // ========================================================
    // 2. CREATING A NEW GRC PROJECT
    // ========================================================

    public int createProject(
            String projectName,
            String description,
            String category)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        int rowsInserted;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(CREATE_PROJECT_FILE);
        rowsInserted = 0;

        // ========== PROCESSING ==========

        // I open my database connection.
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            // I supply the project information.
            stmt.setString(1, projectName);
            stmt.setString(2, description);
            stmt.setString(3, category);

            // I save the project in SQLite.
            rowsInserted = stmt.executeUpdate();
        }

        // ========== OUTPUT ==========

        return rowsInserted;
    }

    // ========================================================
    // 3. VIEWING ALL GRC PROJECTS
    // ========================================================

    public List<GrcProject> findAllProjects()
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        List<GrcProject> projects;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(VIEW_PROJECTS_FILE);
        projects = new ArrayList<>();

        // ========== PROCESSING ==========

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql);
             ResultSet results = stmt.executeQuery()) {

            while (results.next()) {

                // I convert each database row into
                // a GrcProject object.
                GrcProject project = new GrcProject(
                        results.getInt("project_id"),
                        results.getString("project_name"),
                        results.getString("description"),
                        results.getString("category"),
                        results.getString("status"),
                        results.getString("created_at")
                );

                projects.add(project);
            }
        }

        // ========== OUTPUT ==========

        return projects;
    }

    // ========================================================
    // 4. CHECKING WHETHER A PROJECT EXISTS
    // ========================================================

    public boolean projectExists(int projectId)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        boolean exists;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(FIND_PROJECT_FILE);
        exists = false;

        // ========== PROCESSING ==========

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            // I search for the selected project ID.
            stmt.setInt(1, projectId);

            try (ResultSet results = stmt.executeQuery()) {

                // I only need to know if a row exists.
                exists = results.next();
            }
        }

        // ========== OUTPUT ==========

        return exists;
    }
}
