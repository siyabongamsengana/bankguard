
package com.bankguard.repository;

// ============================================================
// BANKGUARD - ASSESSMENT REVIEW REPOSITORY
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

import com.bankguard.database.DatabaseManager;
import com.bankguard.database.SqlLoader;
import com.bankguard.model.AssessmentReview;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AssessmentReviewRepository {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private static final String INSERT_REVIEW_FILE =
            "/sql/reviews/insert_review.sql";

    private static final String VIEW_REVIEW_FILE =
            "/sql/reviews/view_review.sql";

    // ========================================================
    // 2. SAVING AN ASSESSMENT REVIEW
    // ========================================================

    public int saveReview(
            int testId,
            int reviewerId,
            String decision,
            String evidenceAssessment,
            String reviewNotes)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String insertSql;
        int rowsInserted;

        // ========== INITIALISATION ==========

        insertSql = SqlLoader.loadSql(INSERT_REVIEW_FILE);
        rowsInserted = 0;

        // ========== PROCESSING ==========

        // I use one database transaction so that
        // my checks and review insertion stay together.
        try (Connection conn = DatabaseManager.connect()) {

            conn.setAutoCommit(false);

            try {

                // I check that the assessment exists
                // and identify who originally tested it.
                int testerId;

                try (PreparedStatement stmt =
                        conn.prepareStatement(
                        "SELECT tested_by " +
                        "FROM grc_control_tests " +
                        "WHERE test_id = ?")) {

                    stmt.setInt(1, testId);

                    try (ResultSet results =
                            stmt.executeQuery()) {

                        if (!results.next()) {

                            throw new IllegalArgumentException(
                                    "Control assessment not found.");
                        }

                        testerId = results.getInt("tested_by");
                    }
                }

                // I require a registered reviewer.
                try (PreparedStatement stmt =
                        conn.prepareStatement(
                        "SELECT 1 FROM users " +
                        "WHERE user_id = ?")) {

                    stmt.setInt(1, reviewerId);

                    try (ResultSet results =
                            stmt.executeQuery()) {

                        if (!results.next()) {

                            throw new IllegalArgumentException(
                                    "Reviewer is not registered.");
                        }
                    }
                }

                // I prevent the original tester
                // from reviewing their own work.
                if (testerId == reviewerId) {

                    throw new IllegalArgumentException(
                            "Self-review is not allowed.");
                }

                // I prevent a second review decision.
                try (PreparedStatement stmt =
                        conn.prepareStatement(
                        "SELECT 1 FROM grc_assessment_reviews " +
                        "WHERE test_id = ?")) {

                    stmt.setInt(1, testId);

                    try (ResultSet results =
                            stmt.executeQuery()) {

                        if (results.next()) {

                            throw new IllegalArgumentException(
                                    "Assessment already reviewed.");
                        }
                    }
                }

                // I require at least one evidence record
                // before approving an assessment.
                if ("Approved".equals(decision)) {

                    try (PreparedStatement stmt =
                            conn.prepareStatement(
                            "SELECT COUNT(*) FROM grc_evidence " +
                            "WHERE test_id = ?")) {

                        stmt.setInt(1, testId);

                        try (ResultSet results =
                                stmt.executeQuery()) {

                            results.next();

                            if (results.getInt(1) == 0) {

                                throw new IllegalArgumentException(
                                        "Approval requires evidence.");
                            }
                        }
                    }
                }

                // I save the reviewer's decision.
                try (PreparedStatement stmt =
                        conn.prepareStatement(insertSql)) {

                    stmt.setInt(1, testId);
                    stmt.setInt(2, reviewerId);
                    stmt.setString(3, decision);
                    stmt.setString(4, evidenceAssessment);
                    stmt.setString(5, reviewNotes);

                    rowsInserted = stmt.executeUpdate();
                }

                // I commit the approved database changes.
                conn.commit();

            } catch (SQLException | RuntimeException e) {

                // I undo the transaction if anything fails.
                try {

                    conn.rollback();

                } catch (SQLException rollbackError) {

                    e.addSuppressed(rollbackError);
                }

                throw e;
            }
        }

        // ========== OUTPUT ==========

        return rowsInserted;
    }

    // ========================================================
    // 3. VIEWING A SAVED ASSESSMENT REVIEW
    // ========================================================

    public AssessmentReview findReviewByTest(int testId)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(VIEW_REVIEW_FILE);

        // ========== PROCESSING ==========

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, testId);

            try (ResultSet results = stmt.executeQuery()) {

                if (results.next()) {

                    return new AssessmentReview(
                            results.getInt("review_id"),
                            results.getInt("test_id"),
                            results.getInt("reviewer_id"),
                            results.getString("decision"),
                            results.getString("evidence_assessment"),
                            results.getString("review_notes"),
                            results.getString("reviewed_at")
                    );
                }
            }
        }

        // I return null if no review has been recorded.
        return null;
    }
}
