
package com.bankguard.repository;

// ============================================================
// BANKGUARD v1.2 - REMEDIATION VERIFICATION REPOSITORY
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

import com.bankguard.database.DatabaseManager;
import com.bankguard.database.SqlLoader;
import com.bankguard.model.RemediationVerification;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RemediationVerificationRepository {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private static final String REGISTER_VERIFICATION_FILE =
            "/sql/verification/register_verification.sql";

    private static final String VIEW_VERIFICATIONS_FILE =
            "/sql/verification/view_verifications.sql";

    // ========================================================
    // 2. REGISTERING AN INDEPENDENT VERIFICATION
    // ========================================================

    public int registerVerification(
            int findingId,
            int reviewerId,
            String decision,
            String evidenceAssessment,
            String notes)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        String findingStatus;
        int findingOwner;
        int actionCount;
        int incompleteCount;
        int rowsInserted;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(
                REGISTER_VERIFICATION_FILE);

        actionCount = 0;
        incompleteCount = 0;
        rowsInserted = 0;

        // ========== PROCESSING ==========

        try (Connection conn = DatabaseManager.connect()) {

            // I use a transaction to keep the
            // verification checks and insertion together.
            conn.setAutoCommit(false);

            try {

                // I check whether the reviewer exists.
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

                // I retrieve the finding and its owner.
                try (PreparedStatement stmt =
                        conn.prepareStatement(
                        "SELECT owner_id, status " +
                        "FROM grc_findings " +
                        "WHERE finding_id = ?")) {

                    stmt.setInt(1, findingId);

                    try (ResultSet results =
                            stmt.executeQuery()) {

                        if (!results.next()) {

                            throw new IllegalArgumentException(
                                    "Finding not found.");
                        }

                        findingOwner =
                                results.getInt("owner_id");

                        findingStatus =
                                results.getString("status");
                    }
                }

                // I prevent verification of closed findings.
                if ("Closed".equals(findingStatus)) {

                    throw new IllegalArgumentException(
                            "Finding is already closed.");
                }

                // I prevent the finding owner
                // from independently verifying the fix.
                if (findingOwner == reviewerId) {

                    throw new IllegalArgumentException(
                            "Finding owner cannot verify the fix.");
                }

                // I count corrective actions and
                // check if any remain incomplete.
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

                        actionCount = results.getInt("total");

                        incompleteCount =
                                results.getInt("incomplete");
                    }
                }

                // I check whether the reviewer was
                // assigned any corrective work.
                try (PreparedStatement stmt =
                        conn.prepareStatement(
                        "SELECT 1 FROM grc_remediation_actions " +
                        "WHERE finding_id = ? " +
                        "AND assigned_to = ? LIMIT 1")) {

                    stmt.setInt(1, findingId);
                    stmt.setInt(2, reviewerId);

                    try (ResultSet results =
                            stmt.executeQuery()) {

                        if (results.next()) {

                            throw new IllegalArgumentException(
                                    "Remediation assignee cannot " +
                                    "verify their own corrective work.");
                        }
                    }
                }

                // I require completed corrective work
                // before recording a Verified decision.
                if ("Verified".equals(decision)) {

                    if (actionCount == 0) {

                        throw new IllegalArgumentException(
                                "Verification requires at least " +
                                "one remediation action.");
                    }

                    if (incompleteCount > 0) {

                        throw new IllegalArgumentException(
                                "All remediation actions must " +
                                "be completed with evidence.");
                    }
                }

                // I preserve earlier rejected decisions.
                // I do not allow another decision after
                // a successful verification at this stage.
                try (PreparedStatement stmt =
                        conn.prepareStatement(
                        "SELECT decision " +
                        "FROM grc_remediation_verifications " +
                        "WHERE finding_id = ? " +
                        "ORDER BY verification_id DESC " +
                        "LIMIT 1")) {

                    stmt.setInt(1, findingId);

                    try (ResultSet results =
                            stmt.executeQuery()) {

                        if (results.next()
                                && "Verified".equals(
                                results.getString("decision"))) {

                            throw new IllegalArgumentException(
                                    "Finding already has a " +
                                    "successful verification.");
                        }
                    }
                }

                // I save the independent decision.
                try (PreparedStatement stmt =
                        conn.prepareStatement(sql)) {

                    stmt.setInt(1, findingId);
                    stmt.setInt(2, reviewerId);
                    stmt.setString(3, decision);
                    stmt.setString(4, evidenceAssessment);
                    stmt.setString(5, notes);

                    rowsInserted = stmt.executeUpdate();
                }

                // I preserve the completed transaction.
                conn.commit();

            } catch (SQLException | RuntimeException e) {

                // I undo changes if a check or insert fails.
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
    // 3. VIEWING VERIFICATION HISTORY
    // ========================================================

    public List<RemediationVerification>
            findVerificationsByFinding(int findingId)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        List<RemediationVerification> verifications;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(VIEW_VERIFICATIONS_FILE);

        verifications = new ArrayList<>();

        // ========== PROCESSING ==========

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, findingId);

            try (ResultSet results = stmt.executeQuery()) {

                while (results.next()) {

                    RemediationVerification verification =
                            new RemediationVerification(
                                    results.getInt(
                                            "verification_id"),
                                    results.getInt("finding_id"),
                                    results.getInt("reviewer_id"),
                                    results.getString("decision"),
                                    results.getString(
                                            "evidence_assessment"),
                                    results.getString(
                                            "verification_notes"),
                                    results.getString("verified_at")
                            );

                    verifications.add(verification);
                }
            }
        }

        // ========== OUTPUT ==========

        return verifications;
    }
}
