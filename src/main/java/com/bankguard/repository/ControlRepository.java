
package com.bankguard.repository;

// ============================================================
// BANKGUARD v1.1 - CONTROL REPOSITORY
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

// I import my database classes and control model.
// I use JDBC to save and retrieve registered controls.

import com.bankguard.database.DatabaseManager;
import com.bankguard.database.SqlLoader;
import com.bankguard.model.Control;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ControlRepository {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private static final String REGISTER_CONTROL_FILE =
            "/sql/controls/register_control.sql";

    private static final String VIEW_CONTROLS_FILE =
            "/sql/controls/view_controls.sql";

    private static final String VIEW_BY_RISK_FILE =
            "/sql/controls/view_controls_by_risk.sql";

    // ========================================================
    // 2. REGISTERING A NEW CONTROL
    // ========================================================

    public int registerControl(
            int riskId,
            String controlName,
            String controlObjective,
            String controlType,
            int ownerId,
            String frequency)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        int rowsInserted;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(REGISTER_CONTROL_FILE);
        rowsInserted = 0;

        // ========== PROCESSING ==========

        // I open my database connection and prepare
        // the instruction for saving a new control.
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            // I identify the risk this control addresses.
            stmt.setInt(1, riskId);

            // I describe the control and its objective.
            stmt.setString(2, controlName);
            stmt.setString(3, controlObjective);

            // I record the type of control.
            stmt.setString(4, controlType);

            // I assign a registered employee as owner.
            stmt.setInt(5, ownerId);

            // I record how often the control operates.
            stmt.setString(6, frequency);

            // I save the control in SQLite.
            rowsInserted = stmt.executeUpdate();
        }

        // ========== OUTPUT ==========

        return rowsInserted;
    }

    // ========================================================
    // 3. VIEWING ALL REGISTERED CONTROLS
    // ========================================================

    public List<Control> findAllControls()
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        List<Control> controls;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(VIEW_CONTROLS_FILE);
        controls = new ArrayList<>();

        // ========== PROCESSING ==========

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql);
             ResultSet results = stmt.executeQuery()) {

            // I turn each database row into a Control object.
            while (results.next()) {

                Control control = mapControl(results);

                controls.add(control);
            }
        }

        // ========== OUTPUT ==========

        return controls;
    }

    // ========================================================
    // 4. VIEWING CONTROLS LINKED TO A RISK
    // ========================================================

    public List<Control> findControlsByRisk(int riskId)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        List<Control> controls;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(VIEW_BY_RISK_FILE);
        controls = new ArrayList<>();

        // ========== PROCESSING ==========

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            // I identify the risk being reviewed.
            stmt.setInt(1, riskId);

            try (ResultSet results = stmt.executeQuery()) {

                while (results.next()) {

                    Control control = mapControl(results);

                    controls.add(control);
                }
            }
        }

        // ========== OUTPUT ==========

        return controls;
    }

    // ========================================================
    // 5. CONVERTING DATABASE ROWS INTO CONTROL OBJECTS
    // ========================================================

    private Control mapControl(ResultSet results)
            throws SQLException {

        // I use one method to build my Control objects.
        // This avoids repeating the same mapping code.

        return new Control(
                results.getInt("control_id"),
                results.getInt("risk_id"),
                results.getString("control_name"),
                results.getString("control_objective"),
                results.getString("control_type"),
                results.getInt("owner_id"),
                results.getString("frequency"),
                results.getString("status"),
                results.getString("created_at")
        );
    }
}
