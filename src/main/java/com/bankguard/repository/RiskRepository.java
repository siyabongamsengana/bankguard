
package com.bankguard.repository;

// ============================================================
// IMPORTS
// ============================================================

// I use these classes to manage risks and communicate
// with my SQLite database.

// I also use AuditService to record risk changes.

import com.bankguard.database.DatabaseManager;
import com.bankguard.database.SqlLoader;
import com.bankguard.model.Risk;
import com.bankguard.service.AuditService;

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

    // I keep the locations of my SQL files together.

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

    // I use this query to find the values before a change.
    private static final String FIND_RISK_FILE =
            "/sql/risks/find_risk_for_audit.sql";

    // I use my audit service to save evidence of changes.
    private final AuditService auditService;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public RiskRepository() {

        // I initialise the service responsible for
        // recording my audit information.
        auditService = new AuditService();
    }

    // ========================================================
    // 3. CHECKING FOR DUPLICATE RISKS
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
    // 4. SAVING A NEW RISK
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

        // I save my new risk with its selected owner.
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
    // 5. VIEWING ALL REGISTERED RISKS
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

        // I retrieve my risks and their owner information.
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
    // 6. UPDATING RISK STATUS WITH AN AUDIT TRAIL
    // ========================================================

    public int updateStatus(
            int riskId,
            String newStatus,
            int changedBy)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String findSql;
        String updateSql;
        String oldStatus;
        int rowsUpdated;

        // ========== INITIALISATION ==========

        findSql = SqlLoader.loadSql(FIND_RISK_FILE);
        updateSql = SqlLoader.loadSql(UPDATE_STATUS_FILE);

        oldStatus = null;
        rowsUpdated = 0;

        // ========== PROCESSING ==========

        // I open one connection for the whole operation.
        try (Connection conn = DatabaseManager.connect()) {

            // I start a transaction.
            // Changes are not final until I commit them.
            conn.setAutoCommit(false);

            try {

                // I retrieve the risk's current status.
                try (PreparedStatement stmt =
                             conn.prepareStatement(findSql)) {

                    stmt.setInt(1, riskId);

                    try (ResultSet results =
                                 stmt.executeQuery()) {

                        if (results.next()) {
                            oldStatus =
                                    results.getString("status");
                        }
                    }
                }

                // I stop if the risk does not exist.
                if (oldStatus == null) {
                    conn.rollback();
                    return 0;
                }

                // I do not update an unchanged status.
                if (oldStatus.equals(newStatus)) {
                    conn.rollback();
                    return 0;
                }

                // I update the risk using the same connection.
                try (PreparedStatement stmt =
                             conn.prepareStatement(updateSql)) {

                    stmt.setString(1, newStatus);
                    stmt.setInt(2, riskId);

                    rowsUpdated = stmt.executeUpdate();
                }

                // I only record the change if the
                // risk was actually updated.
                if (rowsUpdated > 0) {

                    auditService.recordChange(
                            conn,
                            riskId,
                            "STATUS_CHANGED",
                            oldStatus,
                            newStatus,
                            changedBy
                    );
                }

                // I commit the update and its audit record.
                // Both become permanent together.
                conn.commit();

            } catch (SQLException | IOException
                     | RuntimeException e) {

                // I undo the update if either operation fails.
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;
            }
        }

        // ========== OUTPUT ==========

        return rowsUpdated;
    }

    // ========================================================
    // 7. CHANGING RISK OWNERSHIP WITH AN AUDIT TRAIL
    // ========================================================

    public int updateRiskOwner(
            int riskId,
            int newOwnerId,
            int changedBy)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String findSql;
        String updateSql;
        Integer oldOwnerId;
        int rowsUpdated;

        // ========== INITIALISATION ==========

        findSql = SqlLoader.loadSql(FIND_RISK_FILE);
        updateSql = SqlLoader.loadSql(UPDATE_OWNER_FILE);

        oldOwnerId = null;
        rowsUpdated = 0;

        // ========== PROCESSING ==========

        // I use one connection for the ownership update
        // and the audit record.
        try (Connection conn = DatabaseManager.connect()) {

            // I begin my database transaction.
            conn.setAutoCommit(false);

            try {

                // I find the risk's current owner.
                try (PreparedStatement stmt =
                             conn.prepareStatement(findSql)) {

                    stmt.setInt(1, riskId);

                    try (ResultSet results =
                                 stmt.executeQuery()) {

                        if (results.next()) {
                            oldOwnerId =
                                    results.getInt("owner_id");
                        }
                    }
                }

                // I reject a risk ID that does not exist.
                if (oldOwnerId == null) {
                    conn.rollback();
                    return 0;
                }

                // I do not record a change when the
                // selected owner is already responsible.
                if (oldOwnerId == newOwnerId) {
                    conn.rollback();
                    return 0;
                }

                // I update the owner in the risks table.
                try (PreparedStatement stmt =
                             conn.prepareStatement(updateSql)) {

                    // My SQL expects the new owner first.
                    stmt.setInt(1, newOwnerId);

                    // The risk ID comes second.
                    stmt.setInt(2, riskId);

                    rowsUpdated = stmt.executeUpdate();
                }

                // I record both the old and new owner IDs.
                if (rowsUpdated > 0) {

                    auditService.recordChange(
                            conn,
                            riskId,
                            "OWNER_CHANGED",
                            String.valueOf(oldOwnerId),
                            String.valueOf(newOwnerId),
                            changedBy
                    );
                }

                // I make both changes permanent together.
                conn.commit();

            } catch (SQLException | IOException
                     | RuntimeException e) {

                // If anything fails, I undo the update.
                // This prevents unaudited ownership changes.
                try {
                    conn.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;
            }
        }

        // ========== OUTPUT ==========

        return rowsUpdated;
    }
}
