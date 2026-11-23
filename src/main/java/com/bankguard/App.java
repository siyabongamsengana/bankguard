
package com.bankguard;

// ============================================================
// IMPORTS
// ============================================================

// I import Scanner because I want employees
// to interact with BankGuard using the keyboard.
import java.util.Scanner;

public class App {

    public static void main(String[] args) {

        // ====================================================
        // 1. DECLARATIONS
        // ====================================================

        // I use Scanner to receive keyboard input.
        Scanner input;

        // I store the menu option selected by the employee.
        String choice;

        // I use these variables when registering a new risk.
        String riskName;
        String riskDescription;
        int likelihood;
        int impact;

        // I use this variable to identify the risk owner.
        int riskOwnerId;

        // I use these variables when updating a risk status.
        int riskId;
        String riskStatus;
        String statusChoice;

        // I use this variable to keep BankGuard running.
        boolean running;

        // ====================================================
        // 2. INITIALISATION
        // ====================================================

        // I prepare Scanner so I can read keyboard input.
        input = new Scanner(System.in);

        // I want BankGuard to continue until I choose Exit.
        running = true;

        // For now, I assign risks to my fictional
        // IT Operations Manager, Lerato.
        riskOwnerId = 1;

        // I prepare my SQLite database before
        // allowing employees to use the application.
        DatabaseManager.createTables();

        // ====================================================
        // 3. MAIN PROGRAM
        // ====================================================

        // I use this loop to keep displaying the menu
        // until the employee chooses to exit BankGuard.
        while (running) {

            // ========== DISPLAYING MY MENU ==========

            System.out.println(
                    "\n========== BANKGUARD ==========");

            System.out.println("1. Register a new IT risk");
            System.out.println("2. View registered risks");
            System.out.println("3. Update risk status");
            System.out.println("4. Exit");

            System.out.print("\nChoose an option: ");

            // I read the menu option selected by the employee.
            choice = input.nextLine();

            // ========== PROCESSING MY MENU ==========

            // I use switch to decide which action
            // BankGuard should perform.
            switch (choice) {

                // ============================================
                // OPTION 1: REGISTERING A NEW RISK
                // ============================================

                case "1" -> {

                    // I ask the employee to enter
                    // the information about the new risk.
                    System.out.print("\nRisk name: ");
                    riskName = input.nextLine();

                    System.out.print("Risk description: ");
                    riskDescription = input.nextLine();

                    try {

                        // I ask for the likelihood rating.
                        System.out.print("Likelihood (1-5): ");
                        likelihood = Integer.parseInt(
                                input.nextLine());

                        // I ask for the impact rating.
                        System.out.print("Impact (1-5): ");
                        impact = Integer.parseInt(
                                input.nextLine());

                        // I send the information to my
                        // DatabaseManager so it can be validated
                        // and saved in SQLite.
                        DatabaseManager.addRisk(
                                riskName,
                                riskDescription,
                                likelihood,
                                impact,
                                riskOwnerId
                        );

                    } catch (NumberFormatException e) {

                        // If letters are entered instead of
                        // numbers, I display an error message.
                        System.out.println(
                                "Please enter valid whole numbers.");
                    }
                }

                // ============================================
                // OPTION 2: VIEWING MY REGISTERED RISKS
                // ============================================

                case "2" -> {

                    // I retrieve all the risks I have
                    // already saved in my SQLite database.
                    DatabaseManager.viewRisks();
                }

                // ============================================
                // OPTION 3: UPDATING A RISK STATUS
                // ============================================

                case "3" -> {

                    // I first display the existing risks
                    // so I can choose the correct risk ID.
                    DatabaseManager.viewRisks();

                    try {

                        // I ask which risk needs an update.
                        System.out.print(
                                "\nEnter the risk ID number: ");

                        riskId = Integer.parseInt(
                                input.nextLine());

                        // I display the available statuses
                        // so the employee knows what to select.
                        System.out.println("\nAvailable Statuses:");
                        System.out.println("1. Open");
                        System.out.println("2. In Progress");
                        System.out.println("3. Mitigated");
                        System.out.println("4. Closed");

                        System.out.print(
                                "\nChoose the new status: ");

                        // I read the selected status option.
                        statusChoice = input.nextLine();

                        // ========== STATUS SELECTION ==========

                        // I convert the number selected
                        // into the correct status name.
                        switch (statusChoice) {

                            case "1" ->
                                riskStatus = "Open";

                            case "2" ->
                                riskStatus = "In Progress";

                            case "3" ->
                                riskStatus = "Mitigated";

                            case "4" ->
                                riskStatus = "Closed";

                            default ->
                                riskStatus = null;
                        }

                        // ========== STATUS VALIDATION ==========

                        // I check whether a valid status
                        // was selected before updating the risk.
                        if (riskStatus == null) {

                            System.out.println(
                                    "Invalid status selection.");

                        } else {

                            // I send the risk ID and status
                            // to DatabaseManager for updating.
                            DatabaseManager.updateRiskStatus(
                                    riskId, riskStatus);
                        }

                    } catch (NumberFormatException e) {

                        // If the risk ID is not a valid number,
                        // I display an error instead of crashing.
                        System.out.println(
                                "Please enter a valid risk ID.");
                    }
                }

                // ============================================
                // OPTION 4: EXITING BANKGUARD
                // ============================================

                case "4" -> {

                    // I change running to false
                    // so my while loop stops.
                    running = false;

                    // I display a message before exiting.
                    System.out.println(
                            "\nThank you for using BankGuard.");
                }

                // ============================================
                // INVALID MENU OPTION
                // ============================================

                default -> {

                    // If an option outside the menu is entered,
                    // I display a message and show the menu again.
                    System.out.println(
                            "Invalid option. Please try again.");
                }
            }
        }

        // ====================================================
        // 4. END OF PROGRAM
        // ====================================================

        // I close Scanner because I no longer
        // need to receive keyboard input.
        input.close();
    }
}





