
package com.bankguard.repository;

// ============================================================
// BANKGUARD v1.1 - CONTROL TEST REPOSITORY
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

// I use this repository to save and retrieve assessments
// performed on my registered IT and security controls.

import com.bankguard.database.DatabaseManager;
import com.bankguard.database.SqlLoader;
import com.bankguard.model.ControlTest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ControlTestRepository {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private static final String REGISTER_TEST_FILE =
            "/sql/control_tests/register_control_test.sql";

    private static final String VIEW_TESTS_FILE =
            "/sql/control_tests/view_control_tests.sql";

    // ========================================================
    // 2. REGISTERING A CONTROL TEST
    // ========================================================

    public int registerControlTest(
            int controlId,
            String testProcedure,
            String assessmentPeriod,
            String testResults,
            String conclusion,
            int testedBy)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        int rowsInserted;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(REGISTER_TEST_FILE);
        rowsInserted = 0;

        // ========== PROCESSING ==========

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            // I identify the control being assessed.
            stmt.setInt(1, controlId);

            // I document how I tested the control.
            stmt.setString(2, testProcedure);

            // I record the assessment period.
            stmt.setString(3, assessmentPeriod);

            // I record my observations and test results.
            stmt.setString(4, testResults);

            // I record my assessment conclusion.
            stmt.setString(5, conclusion);

            // I record the employee ID supplied
            // for the person performing the test.
            stmt.setInt(6, testedBy);

            // I save the assessment in SQLite.
            rowsInserted = stmt.executeUpdate();
        }

        // ========== OUTPUT ==========

        return rowsInserted;
    }

    // ========================================================
    // 3. VIEWING TESTS FOR A PARTICULAR CONTROL
    // ========================================================

    public List<ControlTest> findTestsByControl(int controlId)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        List<ControlTest> tests;

        // ========== INITIALISATION ==========

        sql = SqlLoader.loadSql(VIEW_TESTS_FILE);
        tests = new ArrayList<>();

        // ========== PROCESSING ==========

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            // I select the control being reviewed.
            stmt.setInt(1, controlId);

            try (ResultSet results = stmt.executeQuery()) {

                // I retrieve every recorded assessment.
                while (results.next()) {

                    ControlTest test = new ControlTest(
                            results.getInt("test_id"),
                            results.getInt("control_id"),
                            results.getString("test_procedure"),
                            results.getString("assessment_period"),
                            results.getString("test_results"),
                            results.getString("conclusion"),
                            results.getInt("tested_by"),
                            results.getString("tested_at")
                    );

                    tests.add(test);
                }
            }
        }

        // ========== OUTPUT ==========

        return tests;
    }
}
