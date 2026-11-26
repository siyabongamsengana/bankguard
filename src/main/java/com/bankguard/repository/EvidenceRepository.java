
package com.bankguard.repository;

// ============================================================
// BANKGUARD v1.1 - EVIDENCE REPOSITORY
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

// I use this repository to record references to
// evidence reviewed during control assessments.

// I store evidence information, not actual files.

import com.bankguard.database.DatabaseManager;
import com.bankguard.database.SqlLoader;
import com.bankguard.model.Evidence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EvidenceRepository {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private static final String REGISTER_EVIDENCE_FILE =
            "/sql/evidence/register_evidence.sql";

    private static final String VIEW_EVIDENCE_FILE =
            "/sql/evidence/view_evidence.sql";

    // ========================================================
    // 2. REGISTERING EVIDENCE
    // ========================================================

    public int registerEvidence(
            int testId,
            String evidenceName,
            String evidenceSource,
            String evidenceDescription,
            String evidenceReference)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        int rowsInserted;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(REGISTER_EVIDENCE_FILE);
        rowsInserted = 0;

        // ========== PROCESSING ==========

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            // I link the evidence to a specific control test.
            stmt.setInt(1, testId);

            // I record the name of the evidence item.
            stmt.setString(2, evidenceName);

            // I document where the evidence came from.
            stmt.setString(3, evidenceSource);

            // I explain what information was reviewed.
            stmt.setString(4, evidenceDescription);

            // I save a reference for future review.
            stmt.setString(5, evidenceReference);

            // I save my evidence record.
            rowsInserted = stmt.executeUpdate();
        }

        // ========== OUTPUT ==========

        return rowsInserted;
    }

    // ========================================================
    // 3. VIEWING EVIDENCE FOR A CONTROL TEST
    // ========================================================

    public List<Evidence> findEvidenceByTest(int testId)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        List<Evidence> evidenceRecords;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(VIEW_EVIDENCE_FILE);
        evidenceRecords = new ArrayList<>();

        // ========== PROCESSING ==========

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            // I identify the assessment whose evidence
            // I want to retrieve.
            stmt.setInt(1, testId);

            try (ResultSet results = stmt.executeQuery()) {

                while (results.next()) {

                    // I convert the saved row into
                    // an Evidence object.
                    Evidence evidence = new Evidence(
                            results.getInt("evidence_id"),
                            results.getInt("test_id"),
                            results.getString("evidence_name"),
                            results.getString("evidence_source"),
                            results.getString(
                                    "evidence_description"),
                            results.getString(
                                    "evidence_reference"),
                            results.getString("recorded_at")
                    );

                    evidenceRecords.add(evidence);
                }
            }
        }

        // ========== OUTPUT ==========

        return evidenceRecords;
    }
}

