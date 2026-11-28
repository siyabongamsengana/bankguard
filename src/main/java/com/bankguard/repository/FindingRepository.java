
package com.bankguard.repository;

// ============================================================
// BANKGUARD v1.2 - FINDING REPOSITORY
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

import com.bankguard.database.DatabaseManager;
import com.bankguard.database.SqlLoader;
import com.bankguard.model.Finding;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FindingRepository {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private static final String REGISTER_FINDING_FILE =
            "/sql/findings/register_finding.sql";

    private static final String VIEW_FINDINGS_FILE =
            "/sql/findings/view_findings.sql";

    // ========================================================
    // 2. CHECKING IF AN ASSESSMENT EXISTS
    // ========================================================

    public boolean assessmentExists(int testId)
            throws SQLException {

        // I check the database directly instead of
        // searching through every control assessment.

        String sql =
                "SELECT 1 FROM grc_control_tests " +
                "WHERE test_id = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, testId);

            try (ResultSet results = stmt.executeQuery()) {

                return results.next();
            }
        }
    }

    // ========================================================
    // 3. REGISTERING A FINDING
    // ========================================================

    public int registerFinding(
            int testId,
            String title,
            String description,
            String severity,
            int ownerId,
            String dueDate)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        int rowsInserted;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(REGISTER_FINDING_FILE);

        rowsInserted = 0;

        // ========== PROCESSING ==========

        // I use a prepared statement to protect
        // my SQL query from unsafe input.
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, testId);
            stmt.setString(2, title);
            stmt.setString(3, description);
            stmt.setString(4, severity);
            stmt.setInt(5, ownerId);
            stmt.setString(6, dueDate);

            // I save the finding to SQLite.
            rowsInserted = stmt.executeUpdate();
        }

        // ========== OUTPUT ==========

        return rowsInserted;
    }

    // ========================================================
    // 4. RETRIEVING ALL REGISTERED FINDINGS
    // ========================================================

    public List<Finding> findAllFindings()
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        List<Finding> findings;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(VIEW_FINDINGS_FILE);
        findings = new ArrayList<>();

        // ========== PROCESSING ==========

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql);
             ResultSet results = stmt.executeQuery()) {

            while (results.next()) {

                // I convert each saved database row
                // into a Finding model object.
                Finding finding = new Finding(
                        results.getInt("finding_id"),
                        results.getInt("test_id"),
                        results.getString("finding_title"),
                        results.getString("finding_description"),
                        results.getString("severity"),
                        results.getInt("owner_id"),
                        results.getString("due_date"),
                        results.getString("status"),
                        results.getString("created_at")
                );

                findings.add(finding);
            }
        }

        // ========== OUTPUT ==========

        return findings;
    }
}

