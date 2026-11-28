
package com.bankguard.repository;

// ============================================================
// BANKGUARD v1.2 - REMEDIATION ACTION REPOSITORY
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

// I import my database classes and remediation model.
// I use JDBC to save, retrieve and update corrective actions.

import com.bankguard.database.DatabaseManager;
import com.bankguard.database.SqlLoader;
import com.bankguard.model.RemediationAction;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RemediationActionRepository {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    // I store the locations of my remediation SQL files.

    private static final String REGISTER_ACTION_FILE =
            "/sql/remediation/register_action.sql";

    private static final String VIEW_ACTIONS_FILE =
            "/sql/remediation/view_actions.sql";

    // I load the SQL used to complete corrective work.
    private static final String COMPLETE_ACTION_FILE =
            "/sql/remediation/complete_action.sql";

    // ========================================================
    // 2. CHECKING WHETHER A FINDING EXISTS
    // ========================================================

    public boolean findingExists(int findingId)
            throws SQLException {

        // ========== DECLARATIONS ==========

        String sql;
        boolean exists;

        // ========== INITIALISATION ==========

        sql = "SELECT 1 FROM grc_findings " +
              "WHERE finding_id = ?";

        exists = false;

        // ========== PROCESSING ==========

        // I check whether the selected finding exists
        // before assigning corrective work to it.

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, findingId);

            try (ResultSet results = stmt.executeQuery()) {

                exists = results.next();
            }
        }

        // ========== OUTPUT ==========

        return exists;
    }

    // ========================================================
    // 3. REGISTERING A REMEDIATION ACTION
    // ========================================================

    public int registerAction(
            int findingId,
            String description,
            int assignedTo,
            String dueDate)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        int rowsInserted;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(REGISTER_ACTION_FILE);
        rowsInserted = 0;

        // ========== PROCESSING ==========

        // I use a prepared statement to safely save
        // the corrective action in my SQLite database.

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            // I identify the finding being addressed.
            stmt.setInt(1, findingId);

            // I describe the corrective action.
            stmt.setString(2, description);

            // I assign a registered employee.
            stmt.setInt(3, assignedTo);

            // I record the expected completion date.
            stmt.setString(4, dueDate);

            // I save the corrective action.
            rowsInserted = stmt.executeUpdate();
        }

        // ========== OUTPUT ==========

        return rowsInserted;
    }

    // ========================================================
    // 4. VIEWING ACTIONS LINKED TO A FINDING
    // ========================================================

    public List<RemediationAction> findActionsByFinding(
            int findingId)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        List<RemediationAction> actions;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(VIEW_ACTIONS_FILE);
        actions = new ArrayList<>();

        // ========== PROCESSING ==========

        // I retrieve all corrective actions belonging
        // to the selected finding.

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, findingId);

            try (ResultSet results = stmt.executeQuery()) {

                while (results.next()) {

                    // I convert each saved database row
                    // into a RemediationAction object.

                    RemediationAction action =
                            new RemediationAction(
                                    results.getInt("action_id"),
                                    results.getInt("finding_id"),
                                    results.getString(
                                            "action_description"),
                                    results.getInt("assigned_to"),
                                    results.getString("due_date"),
                                    results.getString("status"),
                                    results.getString(
                                            "completion_evidence"),
                                    results.getString("created_at")
                            );

                    actions.add(action);
                }
            }
        }

        // ========== OUTPUT ==========

        return actions;
    }

    // ========================================================
    // 5. COMPLETING A REMEDIATION ACTION
    // ========================================================

    public int completeAction(
            int actionId,
            int actingEmployeeId,
            String evidenceReference)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        int rowsUpdated;

        // ========== INITIALISATION ==========

        // I load the SQL instruction used to
        // mark a corrective action as completed.

        sql = SqlLoader.loadSql(COMPLETE_ACTION_FILE);

        rowsUpdated = 0;

        // ========== PROCESSING ==========

        // I use one SQL UPDATE to save the completion
        // status and its evidence reference together.

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            // I record the reference to evidence
            // supporting the completed corrective work.
            stmt.setString(1, evidenceReference);

            // I identify the action being completed.
            stmt.setInt(2, actionId);

            // I check that the supplied employee ID
            // matches the employee assigned to the action.
            stmt.setInt(3, actingEmployeeId);

            // I update the action only if all the
            // conditions in my SQL instruction pass.
            rowsUpdated = stmt.executeUpdate();
        }

        // ========== OUTPUT ==========

        return rowsUpdated;
    }
}
