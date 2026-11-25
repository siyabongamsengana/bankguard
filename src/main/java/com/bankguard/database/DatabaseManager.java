
package com.bankguard.database;

// ============================================================
// IMPORTS
// ============================================================

// I import these classes to connect to SQLite,
// create my tables and upgrade older databases.

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

    // I keep the locations of my SQL files here.
    private static final String SCHEMA_FILE =
            "/sql/schema.sql";

    private static final String STATUS_MIGRATION_FILE =
            "/sql/migrations/add_status.sql";

    // I use this SQL file to create my audit trail table.
    private static final String AUDIT_MIGRATION_FILE =
            "/sql/migrations/add_audit_logs.sql";

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

        // I enable foreign keys so that relationships
        // between my database tables are enforced.
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
    // 3. LOADING MY DATABASE SCHEMA
    // ========================================================

    private static void loadSchema(Connection conn)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sqlContent;
        String[] sqlStatements;
        StringBuilder cleanSql;

        // ========== INITIALISATION ==========

        // I read the table definitions from schema.sql.
        sqlContent = SqlLoader.loadSql(SCHEMA_FILE);

        cleanSql = new StringBuilder();

        // ========== PROCESSING ==========

        // I remove full-line SQL comments.
        for (String line : sqlContent.split("\\R")) {

            if (!line.stripLeading().startsWith("--")) {

                cleanSql.append(line).append("\n");
            }
        }

        // I separate my SQL statements at semicolons.
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
    // 4. UPGRADING MY EXISTING DATABASE
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

        // I only add the column when it is missing.
        // This protects my previously saved risk records.
        if (!statusExists) {

            sql = SqlLoader.loadSql(STATUS_MIGRATION_FILE);

            try (Statement stmt = conn.createStatement()) {

                stmt.execute(sql);
            }
        }
    }

    // ========================================================
    // 5. CREATING MY AUDIT TRAIL TABLE - v0.9
    // ========================================================

    private static void ensureAuditTable(Connection conn)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sqlContent;
        String[] sqlStatements;
        StringBuilder cleanSql;

        // ========== INITIALISATION ==========

        // I load the SQL file that defines my audit table.
        sqlContent = SqlLoader.loadSql(AUDIT_MIGRATION_FILE);

        cleanSql = new StringBuilder();

        // ========== PROCESSING ==========

        // I remove comments so SQLite only receives
        // the SQL instructions.
        for (String line : sqlContent.split("\\R")) {

            if (!line.stripLeading().startsWith("--")) {

                cleanSql.append(line).append("\n");
            }
        }

        // My migration contains CREATE TABLE and
        // CREATE INDEX statements.
        // I execute each statement separately.
        sqlStatements = cleanSql.toString().split(";");

        try (Statement stmt = conn.createStatement()) {

            for (String sql : sqlStatements) {

                if (!sql.isBlank()) {

                    stmt.execute(sql);
                }
            }
        }

        // I use CREATE TABLE IF NOT EXISTS in my SQL file,
        // so existing audit records are not deleted.
    }

    // ========================================================
    // 6. PREPARING MY DATABASE
    // ========================================================

    public boolean createTables() {

        // ========== DECLARATIONS ==========

        String addUserSql;

        // ========== PROCESSING ==========

        try (Connection conn = connect()) {

            // I create my original database tables.
            loadSchema(conn);

            // I upgrade older databases when needed.
            ensureStatusColumn(conn);

            // I create the audit_logs table for v0.9.
            ensureAuditTable(conn);

            // I load my existing employee SQL instruction.
            addUserSql = SqlLoader.loadSql(ADD_USER_FILE);

            // I add my fictional employee if needed.
            try (Statement stmt = conn.createStatement()) {

                stmt.executeUpdate(addUserSql);
            }

            // ========== OUTPUT ==========

            System.out.println(
                    "BankGuard database created successfully!");

            return true;

        } catch (SQLException | IOException e) {

            // I display the reason if setup fails.
            System.out.println(
                    "Database error: " + e.getMessage());

            return false;
        }
    }
}