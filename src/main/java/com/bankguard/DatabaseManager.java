
package com.bankguard;

// ========== IMPORTS ==========

// These allow Java to work with our SQLite database.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseManager {

    // ========== DECLARATIONS ==========

    // This is where our database is stored.
    private static final String DB_URL =
            "jdbc:sqlite:bankguard.db";

    // This instruction creates our users table.
    private static final String USERS_TABLE = """
        CREATE TABLE IF NOT EXISTS users (
            user_id INTEGER PRIMARY KEY,
            name TEXT NOT NULL,
            role TEXT NOT NULL
        )
        """;

    // This instruction creates our risks table.
    private static final String RISKS_TABLE = """
        CREATE TABLE IF NOT EXISTS risks (
            risk_id INTEGER PRIMARY KEY,
            title TEXT NOT NULL,
            description TEXT NOT NULL,

            likelihood INTEGER NOT NULL
                CHECK (likelihood BETWEEN 1 AND 5),

            impact INTEGER NOT NULL
                CHECK (impact BETWEEN 1 AND 5),

            risk_score INTEGER GENERATED ALWAYS AS
                (likelihood * impact) VIRTUAL,

            owner_id INTEGER,

            FOREIGN KEY (owner_id)
                REFERENCES users(user_id)
        )
        """;

    // This instruction adds our fictional employee.
    private static final String ADD_USER = """
        INSERT OR IGNORE INTO users
        (user_id, name, role)
        VALUES (1, 'Lerato', 'IT Operations Manager')
        """;

    // This instruction saves a risk in the database.
    private static final String ADD_RISK = """
        INSERT INTO risks
        (title, description, likelihood, impact, owner_id)
        VALUES (?, ?, ?, ?, ?)
        """;


    // ========== DATABASE CONNECTION ==========

    // This method connects BankGuard to SQLite.
    public static Connection connect()
            throws SQLException {

        // Declare our connection variable.
        Connection connection;

        // Open the database connection.
        connection = DriverManager.getConnection(DB_URL);

        // Make sure table relationships are followed.
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }

        // Return the connection for other methods to use.
        return connection;
    }


    // ========== CREATING DATABASE TABLES ==========

    // This method creates the tables and our test employee.
    public static void createTables() {

        // Connect to our database.
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            // Create the users table.
            stmt.execute(USERS_TABLE);

            // Create the risks table.
            stmt.execute(RISKS_TABLE);

            // Add our first employee if not already there.
            stmt.executeUpdate(ADD_USER);

            // Show that the database setup worked.
            System.out.println(
                    "BankGuard database created successfully!");

        } catch (SQLException e) {

            // Show the error if something goes wrong.
            System.out.println(
                    "Database error: " + e.getMessage());
        }
    }


    // ========== REGISTERING AN IT RISK ==========

    // This method allows BankGuard to save a new risk.
    public static void addRisk(
            String title,
            String description,
            int likelihood,
            int impact,
            int ownerId) {

        // Check that the risk has a name and description.
        if (title == null || title.isBlank()
                || description == null
                || description.isBlank()) {

            System.out.println(
                    "Risk name and description are required.");
            return;
        }

        // Check if the ratings follow our 1 to 5 scale.
        if (likelihood < 1 || likelihood > 5
                || impact < 1 || impact > 5) {

            System.out.println(
                    "Likelihood and impact must be 1 to 5.");
            return;
        }

        // Connect to SQLite to save our risk.
        try (Connection conn = connect();
             PreparedStatement stmt =
                     conn.prepareStatement(ADD_RISK)) {

            // Add the risk information to our SQL statement.
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setInt(3, likelihood);
            stmt.setInt(4, impact);
            stmt.setInt(5, ownerId);

            // Save the information in the risks table.
            stmt.executeUpdate();

            // Tell us that the risk was saved.
            System.out.println(
                    "Risk registered successfully!");

        } catch (SQLException e) {

            // Show the error if the risk cannot be saved.
            System.out.println(
                    "Risk registration failed: " + e.getMessage());
        }
    }

    // ========== VIEWING REGISTERED RISKS ==========

    // This method gets the risks we saved in SQLite.
    public static void viewRisks() {

        // ========== DECLARATIONS ==========

        // This SQL instruction gets the risk information.
        // JOIN connects each risk to its owner in users.
        String sql = """
            SELECT r.risk_id, r.title, r.description,
                   r.likelihood, r.impact, r.risk_score,
                   u.name AS owner_name
            FROM risks r
            LEFT JOIN users u
                ON r.owner_id = u.user_id
            ORDER BY r.risk_id
            """;

        // This helps us check if any risks were found.
        boolean riskFound;


        // ========== INITIALISATION ==========

        // At the beginning, we have not found any risks.
        riskFound = false;


        // ========== PROCESSING ==========

        // Connect to the database and get the saved risks.
        try (Connection conn = connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql);
             ResultSet results = stmt.executeQuery()) {

            // Go through each risk returned by SQLite.
            while (results.next()) {

                // We have found at least one risk.
                riskFound = true;


                // ========== OUTPUT ==========

                // Display the risk information.
                System.out.println("\n--------------------");

                System.out.println("Risk ID: R-"
                        + results.getInt("risk_id"));

                System.out.println("Risk Name: "
                        + results.getString("title"));

                System.out.println("Description: "
                        + results.getString("description"));

                System.out.println("Likelihood: "
                        + results.getInt("likelihood"));

                System.out.println("Impact: "
                        + results.getInt("impact"));

                System.out.println("Risk Score: "
                        + results.getInt("risk_score") + "/25");

                System.out.println("Risk Owner: "
                        + results.getString("owner_name"));
            }

            // If the database has no risks, tell the user.
            if (!riskFound) {
                System.out.println("No risks registered yet.");
            }

        } catch (SQLException e) {

            // Show us the problem if retrieving risks fails.
            System.out.println(
                    "Error viewing risks: " + e.getMessage());
        }
    }

}


