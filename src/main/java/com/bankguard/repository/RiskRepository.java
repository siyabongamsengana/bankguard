
package com.bankguard.repository;

// ============================================================
// IMPORTS
// ============================================================

// I import these classes to work with my risks
// and communicate with my SQLite database.

import com.bankguard.database.DatabaseManager;
import com.bankguard.database.SqlLoader;
import com.bankguard.model.Risk;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RiskRepository {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    // I keep all my risk SQL file locations together.

    private static final String ADD_RISK_FILE =
            "/sql/risks/add_risk.sql";

    private static final String CHECK_DUPLICATE_FILE =
            "/sql/risks/check_duplicate.sql";

    private static final String VIEW_RISKS_FILE =
            "/sql/risks/view_risks.sql";

    private static final String UPDATE_STATUS_FILE =
            "/sql/risks/update_risk_status.sql";

    private static final String UPDATE_OWNER_FILE =
            "/sql/risks/update_risk_owner.sql";


    // ========================================================
    // 2. CHECKING FOR DUPLICATE RISKS
    // ========================================================

    public boolean riskExists(String riskName)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        boolean exists;
        String sql;


        // ========== INITIALISATION ==========

        exists = false;
        sql = SqlLoader.loadSql(CHECK_DUPLICATE_FILE);


        // ========== PROCESSING ==========

        // I check whether the risk title is already stored.
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setString(1, riskName);

            try (ResultSet results = stmt.executeQuery()) {

                if (results.next()) {
                    exists = results.getInt(1) > 0;
                }
            }
        }


        // ========== OUTPUT ==========

        return exists;
    }


    // ========================================================
    // 3. SAVING A NEW RISK
    // ========================================================

    public void saveRisk(
            String title,
            String description,
            int likelihood,
            int impact,
            int ownerId)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;


        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(ADD_RISK_FILE);


        // ========== PROCESSING ==========

        // I save the risk with its selected employee owner.
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setInt(3, likelihood);
            stmt.setInt(4, impact);
            stmt.setInt(5, ownerId);

            stmt.executeUpdate();
        }
    }


    // ========================================================
    // 4. VIEWING ALL REGISTERED RISKS
    // ========================================================

    public List<Risk> findAllRisks()
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        List<Risk> risks;
        String sql;
        Risk risk;


        // ========== INITIALISATION ==========

        risks = new ArrayList<>();
        sql = SqlLoader.loadSql(VIEW_RISKS_FILE);


        // ========== PROCESSING ==========

        // I retrieve each risk and its owner information.
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql);
             ResultSet results = stmt.executeQuery()) {

            while (results.next()) {

                risk = new Risk(
                        results.getInt("risk_id"),
                        results.getString("title"),
                        results.getString("description"),
                        results.getInt("likelihood"),
                        results.getInt("impact"),
                        results.getInt("risk_score"),
                        results.getString("owner_name"),
                        results.getString("status")
                );

                risks.add(risk);
            }
        }


        // ========== OUTPUT ==========

        return risks;
    }


    // ========================================================
    // 5. UPDATING A RISK STATUS
    // ========================================================

    public int updateStatus(int riskId, String newStatus)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        int rowsUpdated;


        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(UPDATE_STATUS_FILE);
        rowsUpdated = 0;


        // ========== PROCESSING ==========

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, riskId);

            rowsUpdated = stmt.executeUpdate();
        }


        // ========== OUTPUT ==========

        return rowsUpdated;
    }


    // ========================================================
    // 6. CHANGING A RISK OWNER
    // ========================================================

    public int updateRiskOwner(int riskId, int employeeId)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        int rowsUpdated;


        // ========== INITIALISATION ==========

        // I load the SQL instruction for changing owners.
        sql = SqlLoader.loadSql(UPDATE_OWNER_FILE);
        rowsUpdated = 0;


        // ========== PROCESSING ==========

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            // I provide the employee who will own the risk.
            stmt.setInt(1, employeeId);

            // I identify the risk I want to reassign.
            stmt.setInt(2, riskId);

            // I save the ownership change.
            rowsUpdated = stmt.executeUpdate();
        }


        // ========== OUTPUT ==========

        // I return how many records were updated.
        return rowsUpdated;
    }
}
