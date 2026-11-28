
package com.bankguard.repository;

// ============================================================
// BANKGUARD v1.2 - FINDING CLOSURE REPOSITORY
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

import com.bankguard.database.DatabaseManager;
import com.bankguard.database.SqlLoader;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FindingClosureRepository {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private static final String REGISTER_CLOSURE_FILE =
            "/sql/closures/register_closure.sql";

    private static final String CLOSE_FINDING_FILE =
            "/sql/closures/close_finding.sql";

    // ========================================================
    // 2. RECORDING A FORMAL FINDING CLOSURE
    // ========================================================

    public int closeFinding(
            int findingId,
            int closedBy,
            String closureNotes)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String insertSql;
        String updateSql;
        String findingStatus;
        String latestDecision;

        int verificationId;
        int actionCount;
        int incompleteCount;
        int rowsInserted;
        int rowsUpdated;

        // ========== INITIALISATION ==========

        insertSql = SqlLoader.loadSql(
                REGISTER_CLOSURE_FILE);

        updateSql = SqlLoader.loadSql(
                CLOSE_FINDING_FILE);

        verificationId = 0;
        actionCount = 0;
        incompleteCount = 0;
        rowsInserted = 0;
        rowsUpdated = 0;

        // ========== PROCESSING ==========

        try (Connection conn = DatabaseManager.connect()) {

            // I use one transaction so the closure record
            // and finding status are saved together.
            conn.setAutoCommit(false);

            try {

                // ====================================================
                // CHECK 1 - FINDING EXISTS AND IS NOT CLOSED
                // ====================================================

                try (PreparedStatement stmt =
                        conn.prepareStatement(
                        "SELECT status FROM grc_findings " +
                        "WHERE finding_id = ?")) {

                    stmt.setInt(1, findingId);

                    try (ResultSet results =
                            stmt.executeQuery()) {

                        if (!results.next()) {

                            throw new IllegalArgumentException(
                                    "Finding not found.");
                        }

                        findingStatus =
                                results.getString("status");
                    }
                }

                if ("Closed".equals(findingStatus)) {

                    throw new IllegalArgumentException(
                            "Finding is already closed.");
                }

                // ====================================================
                // CHECK 2 - CLOSURE EMPLOYEE EXISTS
                // ====================================================

                try (PreparedStatement stmt =
                        conn.prepareStatement(
                        "SELECT 1 FROM users " +
                        "WHERE user_id = ?")) {

                    stmt.setInt(1, closedBy);

                    try (ResultSet results =
                            stmt.executeQuery()) {

                        if (!results.next()) {

                            throw new IllegalArgumentException(
                                    "Closure employee is not registered.");
                        }
                    }
                }

                // ====================================================
                // CHECK 3 - ALL ACTIONS ARE COMPLETE
                // ====================================================

                try (PreparedStatement stmt =
                        conn.prepareStatement(
                        "SELECT COUNT(*) AS total, " +
                        "COALESCE(SUM(CASE " +
                        "WHEN status <> 'Completed' " +
                        "OR completion_evidence IS NULL " +
                        "OR length(trim(completion_evidence)) = 0 " +
                        "THEN 1 ELSE 0 END), 0) AS incomplete " +
                        "FROM grc_remediation_actions " +
                        "WHERE finding_id = ?")) {

                    stmt.setInt(1, findingId);

                    try (ResultSet results =
                            stmt.executeQuery()) {

                        results.next();

                        actionCount =
                                results.getInt("total");

                        incompleteCount =
                                results.getInt("incomplete");
                    }
                }

                if (actionCount == 0) {

                    throw new IllegalArgumentException(
                            "Finding closure requires at least " +
                            "one remediation action.");
                }

                if (incompleteCount > 0) {

                    throw new IllegalArgumentException(
                            "All remediation actions must be " +
                            "completed with evidence before closure.");
                }

                // ====================================================
                // CHECK 4 - LATEST VERIFICATION IS SUCCESSFUL
                // ====================================================

                try (PreparedStatement stmt =
                        conn.prepareStatement(
                        "SELECT verification_id, decision " +
                        "FROM grc_remediation_verifications " +
                        "WHERE finding_id = ? " +
                        "ORDER BY verification_id DESC " +
                        "LIMIT 1")) {

                    stmt.setInt(1, findingId);

                    try (ResultSet results =
                            stmt.executeQuery()) {

                        if (!results.next()) {

                            throw new IllegalArgumentException(
                                    "Independent verification is required.");
                        }

                        verificationId =
                                results.getInt("verification_id");

                        latestDecision =
                                results.getString("decision");
                    }
                }

                if (!"Verified".equals(latestDecision)) {

                    throw new IllegalArgumentException(
                            "Latest remediation verification " +
                            "must be Verified.");
                }

                // ====================================================
                // CHECK 5 - SAVE CLOSURE DECISION
                // ====================================================

                // I link the closure to the exact
                // successful verification record.
                try (PreparedStatement stmt =
                        conn.prepareStatement(insertSql)) {

                    stmt.setInt(1, findingId);
                    stmt.setInt(2, verificationId);
                    stmt.setInt(3, closedBy);
                    stmt.setString(4, closureNotes);

                    rowsInserted = stmt.executeUpdate();
                }

                if (rowsInserted != 1) {

                    throw new SQLException(
                            "Closure record was not saved.");
                }

                // ====================================================
                // CHECK 6 - UPDATE THE FINDING STATUS
                // ====================================================

                try (PreparedStatement stmt =
                        conn.prepareStatement(updateSql)) {

                    stmt.setInt(1, findingId);

                    rowsUpdated = stmt.executeUpdate();
                }

                if (rowsUpdated != 1) {

                    throw new SQLException(
                            "Finding status was not updated.");
                }

                // I commit both changes together.
                conn.commit();

            } catch (SQLException | RuntimeException e) {

                // I prevent a partially saved closure.
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
