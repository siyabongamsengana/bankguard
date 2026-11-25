
package com.bankguard.database;

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

    // I keep the location of my SQLite database here.
    private static final String DB_URL =
            "jdbc:sqlite:bankguard.db";

    // I keep the location of my original database schema.
    private static final String SCHEMA_FILE =
            "/sql/schema.sql";

    // I keep the location of my status migration.
    private static final String STATUS_MIGRATION_FILE =
            "/sql/migrations/add_status.sql";

    // I keep the location of my audit trail migration.
    private static final String AUDIT_MIGRATION_FILE =
            "/sql/migrations/add_audit_logs.sql";

    // I use this migration for my new GRC project tables.
    private static final String PROJECT_MIGRATION_FILE =
            "/sql/migrations/add_grc_projects.sql";

    // I keep the location of my original employee query.
    private static final String ADD_USER_FILE =
            "/sql/users/add_user.sql";

    // ========================================================
    // 2. CONNECTING TO MY DATABASE
    // ========================================================

    public static Connection connect() throws SQLException {

        // ========== DECLARATIONS ==========

        Connection connection;

        // ========== INITIALISATION ==========

        // I open my connection to SQLite.
        connection = DriverManager.getConnection(DB_URL);

        // ========== PROCESSING ==========

        // I enable foreign keys so SQLite can enforce
        // relationships between my database records.
        try (Statement stmt = connection.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON");

        } catch (SQLException e) {

            // I close the connection if setup fails.
            connection.close();
            throw e;
        }

        // ========== OUTPUT ==========

        return connection;
    }

    // ========================================================
    // 3. EXECUTING SQL FILES WITH MULTIPLE STATEMENTS
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

        // I read the SQL instructions from my resource file.
        sqlContent = SqlLoader.loadSql(filePath);

        cleanSql = new StringBuilder();

        // ========== PROCESSING ==========

        // I remove full-line SQL comments before executing
        // my table creation and migration statements.
        for (String line : sqlContent.split("\\R")) {

            if (!line.stripLeading().startsWith("--")) {

                cleanSql.append(line).append("\n");
            }
        }

        // My current schema and migrations use simple
        // SQL statements separated by semicolons.
        // I execute each one separately.
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
    // 4. LOADING MY EXISTING DATABASE SCHEMA
    // ========================================================

    private static void loadSchema(Connection conn)
            throws SQLException, IOException {

        // I create the original BankGuard tables.
        // IF NOT EXISTS protects existing records.
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

        // I check if the status column already exists.
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

        // I add the column only when it is missing.
        if (!statusExists) {

            sql = SqlLoader.loadSql(STATUS_MIGRATION_FILE);

            try (Statement stmt = conn.createStatement()) {

                stmt.execute(sql);
            }
        }
    }

    // ========================================================
    // 6. CREATING MY AUDIT TRAIL TABLE
    // ========================================================

    private static void ensureAuditTable(Connection conn)
            throws SQLException, IOException {

        // I run my existing audit trail migration.
        // This preserves the audit functionality from v0.9.
        executeSqlFile(conn, AUDIT_MIGRATION_FILE);
    }

    // ========================================================
    // 7. CREATING MY GRC PROJECT TABLES - v1.0
    // ========================================================

    private static void ensureProjectTables(Connection conn)
            throws SQLException, IOException {

        // I load my GRC project migration.
        // It creates the project and task tables.

        // I use CREATE TABLE IF NOT EXISTS so
        // previously saved projects are protected.
        executeSqlFile(conn, PROJECT_MIGRATION_FILE);
    }

    // ========================================================
    // 8. PREPARING MY DATABASE
    // ========================================================

    public boolean createTables() {

        // ========== DECLARATIONS ==========

        String addUserSql;

        // ========== PROCESSING ==========

        try (Connection conn = connect()) {

            // I create my original BankGuard tables.
            loadSchema(conn);

            // I maintain compatibility with older records.
            ensureStatusColumn(conn);

            // I initialise my audit trail tables.
            ensureAuditTable(conn);

            // I create my new GRC project and task tables.
            ensureProjectTables(conn);

            // I load my original employee instruction.
            addUserSql = SqlLoader.loadSql(ADD_USER_FILE);

            // I add my fictional employee if required.
            try (Statement stmt = conn.createStatement()) {

                stmt.executeUpdate(addUserSql);
            }

            // ========== OUTPUT ==========

            System.out.println(
                    "BankGuard database created successfully!");

            return true;

        } catch (SQLException | IOException e) {

            // I display an error if database setup fails.
            System.out.println(
                    "Database error: " + e.getMessage());

            return false;
        }
    }
}