
package com.bankguard.database;

// ============================================================
// IMPORTS
// ============================================================

// I import these classes to connect to SQLite,
// execute my schema and upgrade older databases.

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

        // I enable foreign keys so that I cannot assign
        // risks to employees who do not exist.
        try (Statement stmt = connection.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON");

        } catch (SQLException e) {

            // I close my connection if setup fails.
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

        // I remove full-line comments because I only
        // need the SQL instructions when creating tables.
        for (String line : sqlContent.split("\\R")) {

            if (!line.stripLeading().startsWith("--")) {

                cleanSql.append(line).append("\n");
            }
        }

        // My current schema has two simple CREATE TABLE
        // statements, so I separate them at semicolons.
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

        // I assume the status column is missing.
        statusExists = false;

        // ========== PROCESSING ==========

        // I check which columns already exist.
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

        // I only add status if it doesn't exist.
        // This protects my previously saved risk records.
        if (!statusExists) {

            sql = SqlLoader.loadSql(STATUS_MIGRATION_FILE);

            try (Statement stmt = conn.createStatement()) {

                stmt.execute(sql);
            }
        }
    }

    // ========================================================
    // 5. PREPARING MY DATABASE
    // ========================================================

    public boolean createTables() {

        // ========== DECLARATIONS ==========

        String addUserSql;

        // ========== PROCESSING ==========

        try (Connection conn = connect()) {

            // I load my database tables from schema.sql.
            loadSchema(conn);

            // I update older databases when necessary.
            ensureStatusColumn(conn);

            // I load my employee SQL instruction.
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