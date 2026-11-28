
package com.bankguard.database;

// ============================================================
// BANKGUARD v1.2 - DATABASE MANAGER
// FINDINGS, REMEDIATION, VERIFICATION AND CLOSURE
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

// I import these classes to connect to SQLite,
// create tables and upgrade my existing database.

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.IOException;

public class DatabaseManager {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    // I keep my normal BankGuard database location here.
    private static final String DB_URL =
            "jdbc:sqlite:bankguard.db";

    // I allow automated tests to use a separate database.
    private static final String DB_URL_PROPERTY =
            "bankguard.db.url";

    // I keep my original database schema.
    private static final String SCHEMA_FILE =
            "/sql/schema.sql";

    // I maintain compatibility with older risk records.
    private static final String STATUS_MIGRATION_FILE =
            "/sql/migrations/add_status.sql";

    // I preserve my original risk audit trail.
    private static final String AUDIT_MIGRATION_FILE =
            "/sql/migrations/add_audit_logs.sql";

    // I preserve my GRC project workspace.
    private static final String PROJECT_MIGRATION_FILE =
            "/sql/migrations/add_grc_projects.sql";

    // I preserve controls, testing and evidence.
    private static final String CONTROLS_MIGRATION_FILE =
            "/sql/migrations/add_controls_and_evidence.sql";

    // I preserve independent assessment reviews.
    private static final String REVIEWS_MIGRATION_FILE =
            "/sql/migrations/add_assessment_reviews.sql";

    // I preserve findings and corrective actions.
    private static final String FINDINGS_MIGRATION_FILE =
            "/sql/migrations/add_findings_and_remediation.sql";

    // I preserve independent remediation verification.
    private static final String VERIFICATION_MIGRATION_FILE =
            "/sql/migrations/add_remediation_verification.sql";

    // I introduce formal finding closure records.
    private static final String CLOSURE_MIGRATION_FILE =
            "/sql/migrations/add_finding_closures.sql";

    // I keep my original employee seed instruction.
    private static final String ADD_USER_FILE =
            "/sql/users/add_user.sql";

    // ========================================================
    // 2. CONNECTING TO MY DATABASE
    // ========================================================

    public static Connection connect() throws SQLException {

        // ========== DECLARATIONS ==========

        Connection connection;
        String databaseUrl;

        // ========== INITIALISATION ==========

        // I check whether an automated test database
        // has been specified.
        databaseUrl = System.getProperty(
                DB_URL_PROPERTY,
                DB_URL
        );

        // ========== VALIDATION ==========

        if (databaseUrl.isBlank()) {

            throw new SQLException(
                    "Database URL cannot be empty.");
        }

        // ========== PROCESSING ==========

        // I connect to the selected SQLite database.
        connection = DriverManager.getConnection(databaseUrl);

        // I enable foreign key checks for every connection.
        try (Statement stmt = connection.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON");

        } catch (SQLException e) {

            connection.close();
            throw e;
        }

        // ========== OUTPUT ==========

        return connection;
    }

    // ========================================================
    // 3. EXECUTING SQL RESOURCE FILES
    // ========================================================

    private static void executeSqlFile(
            Connection conn,
            String filePath)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sqlContent;
        String[] sqlStatements;
        StringBuilder cleanSql;

        // ========== INITIALISATION ==========

        sqlContent = SqlLoader.loadSql(filePath);
        cleanSql = new StringBuilder();

        // ========== PROCESSING ==========

        // I remove full-line comments from SQL files.
        for (String line : sqlContent.split("\\R")) {

            if (!line.stripLeading().startsWith("--")) {

                cleanSql.append(line).append("\n");
            }
        }

        // I separate the simple SQL statements
        // used in my database migrations.
        sqlStatements = cleanSql.toString().split(";");

        try (Statement stmt = conn.createStatement()) {

            for (String sql : sqlStatements) {

                if (!sql.isBlank()) {

                    stmt.execute(sql);
                }
            }
        }
    }

    // ========================================================
    // 4. LOADING MY ORIGINAL DATABASE SCHEMA
    // ========================================================

    private static void loadSchema(Connection conn)
            throws SQLException, IOException {

        // I create my original BankGuard database tables.
        executeSqlFile(conn, SCHEMA_FILE);
    }

    // ========================================================
    // 5. UPGRADING OLDER RISK RECORDS
    // ========================================================

    private static void ensureStatusColumn(Connection conn)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        boolean statusExists;
        String sql;

        // ========== INITIALISATION ==========

        statusExists = false;

        // ========== PROCESSING ==========

        // I check whether the risks table already
        // contains the status column.
        try (Statement stmt = conn.createStatement();
             ResultSet results =
                     stmt.executeQuery("PRAGMA table_info(risks)")) {

            while (results.next()) {

                if ("status".equalsIgnoreCase(
                        results.getString("name"))) {

                    statusExists = true;
                    break;
                }
            }
        }

        // I add the column only if it is missing.
        if (!statusExists) {

            sql = SqlLoader.loadSql(STATUS_MIGRATION_FILE);

            try (Statement stmt = conn.createStatement()) {

                stmt.execute(sql);
            }
        }
    }

    // ========================================================
    // 6. CREATING RISK AUDIT TABLES
    // ========================================================

    private static void ensureAuditTable(Connection conn)
            throws SQLException, IOException {

        // I preserve risk ownership and status changes.
        executeSqlFile(conn, AUDIT_MIGRATION_FILE);
    }

    // ========================================================
    // 7. CREATING GRC PROJECT TABLES
    // ========================================================

    private static void ensureProjectTables(Connection conn)
            throws SQLException, IOException {

        // I prepare GRC projects and their tasks.
        executeSqlFile(conn, PROJECT_MIGRATION_FILE);
    }

    // ========================================================
    // 8. CREATING CONTROL AND EVIDENCE TABLES
    // ========================================================

    private static void ensureControlTables(Connection conn)
            throws SQLException, IOException {

        // I prepare controls, assessments and evidence.
        executeSqlFile(conn, CONTROLS_MIGRATION_FILE);
    }

    // ========================================================
    // 9. CREATING ASSESSMENT REVIEW TABLES
    // ========================================================

    private static void ensureAssessmentReviewTables(
            Connection conn)
            throws SQLException, IOException {

        // I preserve assessment approvals and rejections.
        executeSqlFile(conn, REVIEWS_MIGRATION_FILE);
    }

    // ========================================================
    // 10. CREATING FINDINGS AND REMEDIATION TABLES
    // ========================================================

    private static void ensureFindingsTables(Connection conn)
            throws SQLException, IOException {

        // I prepare findings and corrective actions.
        executeSqlFile(conn, FINDINGS_MIGRATION_FILE);
    }

    // ========================================================
    // 11. CREATING REMEDIATION VERIFICATION TABLES
    // ========================================================

    private static void ensureVerificationTables(
            Connection conn)
            throws SQLException, IOException {

        // I preserve independent verification decisions
        // and the reviewer's supporting reasoning.
        executeSqlFile(conn, VERIFICATION_MIGRATION_FILE);
    }

    // ========================================================
    // 12. CREATING FINDING CLOSURE TABLES
    // ========================================================

    private static void ensureClosureTables(Connection conn)
            throws SQLException, IOException {

        // I prepare formal closure records while
        // preserving existing findings and reviews.
        executeSqlFile(conn, CLOSURE_MIGRATION_FILE);
    }

    // ========================================================
    // 13. PREPARING MY DATABASE
    // ========================================================

    public boolean createTables() {

        // ========== DECLARATIONS ==========

        String addUserSql;

        // ========== PROCESSING ==========

        try (Connection conn = connect()) {

            // I create my original database tables.
            loadSchema(conn);

            // I upgrade older risk records.
            ensureStatusColumn(conn);

            // I prepare the risk audit trail.
            ensureAuditTable(conn);

            // I prepare GRC projects and tasks.
            ensureProjectTables(conn);

            // I prepare controls, tests and evidence.
            ensureControlTables(conn);

            // I prepare independent assessment reviews.
            ensureAssessmentReviewTables(conn);

            // I prepare findings and corrective actions.
            ensureFindingsTables(conn);

            // I prepare independent remediation verification.
            ensureVerificationTables(conn);

            // I prepare formal finding closure records.
            ensureClosureTables(conn);

            // I seed my original fictional employee.
            addUserSql = SqlLoader.loadSql(ADD_USER_FILE);

            try (Statement stmt = conn.createStatement()) {

                stmt.executeUpdate(addUserSql);
            }

            // ========== OUTPUT ==========

            System.out.println(
                    "BankGuard database created successfully!");

            return true;

        } catch (SQLException | IOException e) {

            System.out.println(
                    "Database error: " + e.getMessage());

            return false;
        }
    }
}


