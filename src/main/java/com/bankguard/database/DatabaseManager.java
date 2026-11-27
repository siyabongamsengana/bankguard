
package com.bankguard.database;

// ============================================================
// BANKGUARD - DATABASE MANAGER
// ASSESSMENT REVIEW DEVELOPMENT
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

    // I keep my original schema resource.
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

    // I preserve my control, testing and evidence tables.
    private static final String CONTROLS_MIGRATION_FILE =
            "/sql/migrations/add_controls_and_evidence.sql";

    // I introduce a separate assessment review table.
    private static final String REVIEWS_MIGRATION_FILE =
            "/sql/migrations/add_assessment_reviews.sql";

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

        // I check whether a test database was specified.
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

        // I enable foreign key checks for each connection.
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

        // I remove full-line comments from my SQL.
        for (String line : sqlContent.split("\\R")) {

            if (!line.stripLeading().startsWith("--")) {

                cleanSql.append(line).append("\n");
            }
        }

        // I separate the simple SQL statements
        // used in my current database migrations.
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
    // 4. LOADING THE ORIGINAL DATABASE SCHEMA
    // ========================================================

    private static void loadSchema(Connection conn)
            throws SQLException, IOException {

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

        executeSqlFile(conn, AUDIT_MIGRATION_FILE);
    }

    // ========================================================
    // 7. CREATING GRC PROJECT TABLES
    // ========================================================

    private static void ensureProjectTables(Connection conn)
            throws SQLException, IOException {

        executeSqlFile(conn, PROJECT_MIGRATION_FILE);
    }

    // ========================================================
    // 8. CREATING CONTROL AND EVIDENCE TABLES
    // ========================================================

    private static void ensureControlTables(Connection conn)
            throws SQLException, IOException {

        executeSqlFile(conn, CONTROLS_MIGRATION_FILE);
    }

    // ========================================================
    // 9. CREATING ASSESSMENT REVIEW TABLES
    // ========================================================

    private static void ensureAssessmentReviewTables(
            Connection conn)
            throws SQLException, IOException {

        // I create the assessment review table
        // without deleting existing control tests.

        executeSqlFile(conn, REVIEWS_MIGRATION_FILE);
    }

    // ========================================================
    // 10. PREPARING MY DATABASE
    // ========================================================

    public boolean createTables() {

        // ========== DECLARATIONS ==========

        String addUserSql;

        // ========== PROCESSING ==========

        try (Connection conn = connect()) {

            // I create my original tables.
            loadSchema(conn);

            // I upgrade older risk records.
            ensureStatusColumn(conn);

            // I preserve the existing risk audit trail.
            ensureAuditTable(conn);

            // I prepare GRC projects and tasks.
            ensureProjectTables(conn);

            // I prepare controls, tests and evidence.
            ensureControlTables(conn);

            // I create the new assessment review table.
            ensureAssessmentReviewTables(conn);

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