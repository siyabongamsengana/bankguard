
package com.bankguard;

// ============================================================
// IMPORTS
// ============================================================

// I import these classes to prepare my database
// and start my console application.

import com.bankguard.database.DatabaseManager;
import com.bankguard.ui.ConsoleMenu;

public class App {

    // ========================================================
    // 1. STARTING BANKGUARD
    // ========================================================

    public static void main(String[] args) {

        // ========== DECLARATIONS ==========

        DatabaseManager databaseManager;
        ConsoleMenu consoleMenu;


        // ========== INITIALISATION ==========

        // I prepare the classes I need to start my system.
        databaseManager = new DatabaseManager();
        consoleMenu = new ConsoleMenu();


        // ========== PROCESSING ==========

        // I prepare my SQLite database before opening
        // the menu so that my tables are ready.
        boolean databaseReady = databaseManager.createTables();

        // I only open the menu when setup succeeds.
        if (databaseReady) {

            consoleMenu.start();

        } else {

            System.out.println(
                    "BankGuard could not start. Check database setup.");
        }
    }
}




