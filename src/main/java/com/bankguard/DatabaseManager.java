
package com.bankguard;

// ============================================================
// IMPORTS
// ============================================================

// I import these classes because I need Java to
// communicate with my SQLite database.
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    // ========== DATABASE LOCATION ==========

    // I store the location of my SQLite database here
    // so I can use it whenever I need a connection.
    private static final String DB_URL =
            "jdbc:sqlite:bankguard.db";


    // ========== USERS TABLE ==========

    // I create this table to store employees and their roles.
    // I will use these employees when assigning risk owners.
    private static final String USERS_TABLE = """
        CREATE TABLE IF NOT EXISTS users (
            user_id INTEGER PRIMARY KEY,
            name TEXT NOT NULL,
            role TEXT NOT NULL
        )
        """;


    // ========== RISKS TABLE ==========

    // I create this table to store my registered IT risks.
    // I limit likelihood and impact to values between 1 and 5.
    // I let SQLite calculate the risk score automatically.
    // I also give every new risk an Open status.
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

            status TEXT NOT NULL DEFAULT 'Open'
                CHECK (status IN (
                    'Open',
                    'In Progress',
                    'Mitigated',
                    'Closed'
                )),

            FOREIGN KEY (owner_id)
                REFERENCES users(user_id)
        )
        """;


    // ========== UPDATING MY EXISTING DATABASE ==========

    // My database already contains risks from earlier lessons.
    // I need to add the status column without deleting them.
    // Existing risks will automatically receive Open.
    private static final String ADD_STATUS_COLUMN = """
        ALTER TABLE risks
        ADD COLUMN status TEXT NOT NULL DEFAULT 'Open'
        CHECK (status IN (
            'Open',
            'In Progress',
            'Mitigated',
            'Closed'
        ))
        """;


    // ========== ADDING MY FIRST EMPLOYEE ==========

    // I add Lerato as my fictional IT Operations Manager.
    // I use INSERT OR IGNORE so I don't add her ID twice.
    private static final String ADD_USER = """
        INSERT OR IGNORE INTO users
        (user_id, name, role)
        VALUES (1, 'Lerato', 'IT Operations Manager')
        """;


    // ========== REGISTERING A RISK ==========

    // I use this instruction whenever I register a new risk.
    // I give new risks an Open status by default.
    private static final String ADD_RISK = """
        INSERT INTO risks
        (title, description, likelihood, impact,
         owner_id, status)
        VALUES (?, ?, ?, ?, ?, 'Open')
        """;


    // ========== CHECKING FOR DUPLICATE RISKS ==========

    // I check whether the same risk title already exists.
    // I ignore capital letters and unnecessary spaces.
    private static final String CHECK_DUPLICATE = """
        SELECT COUNT(*)
        FROM risks
        WHERE LOWER(TRIM(title)) = LOWER(TRIM(?))
        """;


    // ========== VIEWING MY REGISTERED RISKS ==========

    // I retrieve the risk information and the owner's name.
    // I use LEFT JOIN so I can connect risks to employees.
    private static final String VIEW_RISKS = """
        SELECT
            r.risk_id,
            r.title,
            r.description,
            r.likelihood,
            r.impact,
            r.risk_score,
            r.status,
            u.name AS owner_name
        FROM risks r
        LEFT JOIN users u
            ON r.owner_id = u.user_id
        ORDER BY r.risk_id
        """;


    // ========== UPDATING A RISK STATUS ==========

    // I use this instruction when I need to change
    // the status of a risk that is already registered.
    private static final String UPDATE_RISK_STATUS = """
        UPDATE risks
        SET status = ?
        WHERE risk_id = ?
        """;


    // ========================================================
    // 2. DATABASE CONNECTION
    // ========================================================

    public static Connection connect() throws SQLException {

        // ========== DECLARATIONS ==========

        // I declare a variable to hold my database connection.
        Connection connection;


        // ========== INITIALISATION ==========

        // I open a connection to my SQLite database.
        connection = DriverManager.getConnection(DB_URL);


        // ========== PROCESSING ==========

        // I enable foreign keys so I cannot assign a risk
        // to an employee who does not exist in my database.
        try (Statement stmt = connection.createStatement()) {

            stmt.execute("PRAGMA foreign_keys = ON");

        } catch (SQLException e) {

            // If something goes wrong, I close the connection
            // instead of leaving it open unnecessarily.
            connection.close();

            throw e;
        }


        // ========== OUTPUT ==========

        // I return the connection so my other methods
        // can use it to communicate with SQLite.
        return connection;
    }


    // ========================================================
    // 3. PREPARING MY STATUS COLUMN
    // ========================================================

    private static void ensureStatusColumn(Connection conn)
            throws SQLException {

        // ========== DECLARATIONS ==========

        // I use this variable to check whether
        // my risks table already contains a status column.
        boolean statusExists;


        // ========== INITIALISATION ==========

        // I start by assuming the column does not exist.
        statusExists = false;


        // ========== PROCESSING ==========

        // I ask SQLite to show me the columns
        // that already exist in my risks table.
        try (Statement stmt = conn.createStatement();
             ResultSet results =
                     stmt.executeQuery("PRAGMA table_info(risks)")) {

            // I go through the column names one by one.
            while (results.next()) {

                // I check whether I find a column called status.
                if ("status".equalsIgnoreCase(
                        results.getString("name"))) {

                    // If I find it, I don't need to add it again.
                    statusExists = true;
                    break;
                }
            }
        }


        // ========== ADDING A MISSING COLUMN ==========

        // I only add the status column if it is missing.
        // This helps me keep the risks I already registered.
        if (!statusExists) {

            try (Statement stmt = conn.createStatement()) {

                stmt.execute(ADD_STATUS_COLUMN);
            }
        }
    }


    // ========================================================
    // 4. CREATING MY DATABASE TABLES
    // ========================================================

    public static void createTables() {

        // ========== INITIALISATION AND PROCESSING ==========

        // I connect to SQLite before creating my tables.
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            // I create the users table if it does not exist.
            stmt.execute(USERS_TABLE);

            // I create the risks table if it does not exist.
            stmt.execute(RISKS_TABLE);

            // I check whether an older database needs
            // the new status column.
            ensureStatusColumn(conn);

            // I add my fictional employee if needed.
            stmt.executeUpdate(ADD_USER);


            // ========== OUTPUT ==========

            // I display this message when the setup succeeds.
            System.out.println(
                    "BankGuard database created successfully!");

        } catch (SQLException e) {

            // I display the error so I can investigate
            // why my database setup failed.
            System.out.println(
                    "Database error: " + e.getMessage());
        }
    }


    // ========================================================
    // 5. CHECKING FOR EXISTING RISKS
    // ========================================================

    public static boolean riskExists(String riskName)
            throws SQLException {

        // ========== DECLARATIONS ==========

        // I use this variable to remember whether
        // I found a risk with the same title.
        boolean exists;


        // ========== INITIALISATION ==========

        // I start by assuming the risk does not exist.
        exists = false;


        // ========== PROCESSING ==========

        // I connect to my database and search for the title.
        try (Connection conn = connect();
             PreparedStatement stmt =
                     conn.prepareStatement(CHECK_DUPLICATE)) {

            // I give SQLite the risk name I want to check.
            stmt.setString(1, riskName);

            // I get the result of my search.
            try (ResultSet results = stmt.executeQuery()) {

                if (results.next()) {

                    // If the count is above zero,
                    // I know that a matching title exists.
                    exists = results.getInt(1) > 0;
                }
            }
        }


        // ========== OUTPUT ==========

        // I return true if I found a matching risk.
        // Otherwise, I return false.
        return exists;
    }


    // ========================================================
    // 6. REGISTERING A NEW IT RISK
    // ========================================================

    public static void addRisk(
            String title,
            String description,
            int likelihood,
            int impact,
            int ownerId) {

        // ========== VALIDATING REQUIRED INFORMATION ==========

        // Before I save a risk, I check whether
        // the name and description have been provided.
        // I don't want incomplete records in my register.
        if (title == null || title.isBlank()
                || description == null
                || description.isBlank()) {

            System.out.println(
                    "Risk name and description are required.");

            // I stop because the information is incomplete.
            return;
        }


        // ========== VALIDATING RISK RATINGS ==========

        // I only allow values between 1 and 5.
        // This helps me keep my risk assessments consistent.
        if (likelihood < 1 || likelihood > 5
                || impact < 1 || impact > 5) {

            System.out.println(
                    "Likelihood and impact must be 1 to 5.");

            // I stop because the assessment is invalid.
            return;
        }


        // ========== CHECKING FOR DUPLICATES ==========

        // I don't want to register the same risk title twice.
        // I check my database before saving another record.
        try {

            if (riskExists(title)) {

                // If I find a matching title, I warn the user.
                System.out.println(
                        "A risk with this name already exists.");

                // I stop the registration.
                return;
            }

        } catch (SQLException e) {

            // If my duplicate check fails, I don't save
            // the risk because I cannot verify the title.
            System.out.println(
                    "Duplicate check failed: " + e.getMessage());

            return;
        }


        // ========== PROCESSING ==========

        // I connect to SQLite and prepare to save the risk.
        try (Connection conn = connect();
             PreparedStatement stmt =
                     conn.prepareStatement(ADD_RISK)) {

            // I add the risk information to my SQL instruction.
            stmt.setString(1, title.trim());
            stmt.setString(2, description.trim());
            stmt.setInt(3, likelihood);
            stmt.setInt(4, impact);
            stmt.setInt(5, ownerId);

            // I save the risk in my database.
            // SQLite automatically calculates the risk score.
            stmt.executeUpdate();


            // ========== OUTPUT ==========

            // I show this message when the risk is saved.
            System.out.println(
                    "Risk registered successfully!");

        } catch (SQLException e) {

            // If something goes wrong, I display the error.
            System.out.println(
                    "Risk registration failed: " + e.getMessage());
        }
    }


    // ========================================================
    // 7. UPDATING A RISK STATUS
    // ========================================================

    public static void updateRiskStatus(
            int riskId, String newStatus) {

        // ========== DECLARATIONS ==========

        // I use this to check how many records were updated.
        int rowsUpdated;

        // I use this to store a recognised status.
        String validStatus;


        // ========== INITIALISATION ==========

        // I haven't updated any records yet.
        rowsUpdated = 0;

        // I haven't confirmed the selected status yet.
        validStatus = null;


        // ========== VALIDATING THE RISK ID ==========

        // I don't allow negative numbers or zero
        // because my risk IDs start from 1.
        if (riskId <= 0) {

            System.out.println("Invalid risk ID.");
            return;
        }


        // ========== VALIDATING THE STATUS ==========

        // I check whether the selected status
        // is one of the four statuses my system supports.
        if (newStatus != null) {

            switch (newStatus.trim().toLowerCase()) {

                case "open" ->
                    validStatus = "Open";

                case "in progress" ->
                    validStatus = "In Progress";

                case "mitigated" ->
                    validStatus = "Mitigated";

                case "closed" ->
                    validStatus = "Closed";

                default ->
                    validStatus = null;
            }
        }


        // I stop if the status is not recognised.
        if (validStatus == null) {

            System.out.println("Invalid risk status.");
            return;
        }


        // ========== PROCESSING ==========

        // I connect to SQLite to update the selected risk.
        try (Connection conn = connect();
             PreparedStatement stmt =
                     conn.prepareStatement(UPDATE_RISK_STATUS)) {

            // I provide the new status.
            stmt.setString(1, validStatus);

            // I provide the ID of the risk I want to update.
            stmt.setInt(2, riskId);

            // I save the updated information.
            rowsUpdated = stmt.executeUpdate();


            // ========== OUTPUT ==========

            // I check whether SQLite updated a record.
            if (rowsUpdated > 0) {

                System.out.println(
                        "Risk status updated to: " + validStatus);

            } else {

                // If no record was updated,
                // the risk ID probably does not exist.
                System.out.println(
                        "Risk not found. Check the risk ID.");
            }

        } catch (SQLException e) {

            // I display the problem if the update fails.
            System.out.println(
                    "Status update failed: " + e.getMessage());
        }
    }


    // ========================================================
    // 8. VIEWING MY REGISTERED RISKS
    // ========================================================

    public static void viewRisks() {

        // ========== DECLARATIONS ==========

        // I use this to check whether I found any records.
        boolean riskFound;


        // ========== INITIALISATION ==========

        // I haven't found any risks yet.
        riskFound = false;


        // ========== PROCESSING ==========

        // I connect to SQLite and retrieve my saved risks.
        try (Connection conn = connect();
             PreparedStatement stmt =
                     conn.prepareStatement(VIEW_RISKS);
             ResultSet results = stmt.executeQuery()) {

            System.out.println(
                    "\n========== IT RISK REGISTER ==========");

            // I go through my saved risks one at a time.
            while (results.next()) {

                // I now know that at least one risk exists.
                riskFound = true;


                // ========== OUTPUT ==========

                // I display the information in a clear format.
                System.out.println(
                        "\n--------------------------------------");

                System.out.println(
                        "Risk ID: R-"
                        + results.getInt("risk_id"));

                System.out.println(
                        "Risk Name: "
                        + results.getString("title"));

                System.out.println(
                        "Description: "
                        + results.getString("description"));

                System.out.println(
                        "Likelihood: "
                        + results.getInt("likelihood") + "/5");

                System.out.println(
                        "Impact: "
                        + results.getInt("impact") + "/5");

                System.out.println(
                        "Risk Score: "
                        + results.getInt("risk_score") + "/25");

                System.out.println(
                        "Risk Owner: "
                        + results.getString("owner_name"));

                System.out.println(
                        "Status: "
                        + results.getString("status"));
            }


            // ========== NO RISKS FOUND ==========

            // If my database has no risks, I display
            // a message instead of leaving the screen empty.
            if (!riskFound) {

                System.out.println(
                        "No risks registered yet.");
            }

        } catch (SQLException e) {

            // I display the error if I cannot retrieve
            // the information from my database.
            System.out.println(
                    "Error viewing risks: " + e.getMessage());
        }
    }
}

