
package com.bankguard.repository;

// ============================================================
// IMPORTS
// ============================================================

// I import my database classes and audit log model.
// I use JDBC to save and retrieve audit records.

import com.bankguard.database.DatabaseManager;
import com.bankguard.database.SqlLoader;
import com.bankguard.model.AuditLog;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AuditRepository {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    // I keep my audit SQL queries in separate files.

    private static final String INSERT_AUDIT_SQL =
            "/sql/audit/insert_audit_log.sql";

    private static final String VIEW_AUDIT_SQL =
            "/sql/audit/view_audit_logs.sql";

    // ========================================================
    // 2. SAVING AN AUDIT RECORD
    // ========================================================

    public void saveAuditLog(
            Connection connection,
            int riskId,
            String action,
            String oldValue,
            String newValue,
            int changedBy)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;

        // ========== INITIALISATION ==========

        // I load my SQL instruction from the resources.
        sql = SqlLoader.loadSql(INSERT_AUDIT_SQL);

        // ========== PROCESSING ==========

        // I use the connection supplied by my caller.
        // This will let me save the risk update and
        // its audit record in the same transaction.

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            // I identify the risk that was changed.
            statement.setInt(1, riskId);

            // I record the type of change.
            statement.setString(2, action);

            // I record the value before the change.
            statement.setString(3, oldValue);

            // I record the value after the change.
            statement.setString(4, newValue);

            // I record the employee ID supplied.
            statement.setInt(5, changedBy);

            // I save my audit record to SQLite.
            statement.executeUpdate();
        }
    }

    // ========================================================
    // 3. VIEWING AUDIT HISTORY
    // ========================================================

    public List<AuditLog> findByRiskId(int riskId)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        List<AuditLog> auditLogs;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(VIEW_AUDIT_SQL);
        auditLogs = new ArrayList<>();

        // ========== PROCESSING ==========

        // I open a connection to retrieve saved records.
        try (Connection connection = DatabaseManager.connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            // I select the risk whose history I want.
            statement.setInt(1, riskId);

            try (ResultSet results =
                         statement.executeQuery()) {

                // I read every audit record returned.
                while (results.next()) {

                    AuditLog auditLog = new AuditLog(
                            results.getInt("audit_id"),
                            results.getInt("risk_id"),
                            results.getString("action"),
                            results.getString("old_value"),
                            results.getString("new_value"),
                            results.getInt("changed_by"),
                            results.getString("changed_at")
                    );

                    auditLogs.add(auditLog);
                }
            }
        }

        // ========== OUTPUT ==========

        // I return the full history for this risk.
        return auditLogs;
    }
}