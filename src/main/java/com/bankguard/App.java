
package com.bankguard;

// This allows us to receive information from the user.
import java.util.Scanner;

public class App {

    public static void main(String[] args) {

        // ========== DECLARATIONS ==========

        // Scanner allows the user to enter information.
        Scanner input;

        // This stores the menu option selected by the user.
        String choice;

        // These store the information about a risk.
        String riskName;
        String riskDescription;

        // These store our risk assessment values.
        int likelihood;
        int impact;

        // This stores the ID of the risk owner.
        int riskOwnerId;

        // This controls whether the program keeps running.
        boolean running;


        // ========== INITIALISATION ==========

        // Prepare Scanner so we can receive user input.
        input = new Scanner(System.in);

        // The program must continue until the user exits.
        running = true;

        // Our fictional employee has user ID 1.
        riskOwnerId = 1;

        // Start our database before using the system.
        DatabaseManager.createTables();


        // ========== MAIN PROGRAM ==========

        // Keep showing the menu while running is true.
        while (running) {

            // ========== DISPLAY MENU ==========

            System.out.println("\n========= BANKGUARD =========");
            System.out.println("1. Register a new IT risk");
            System.out.println("2. View registered risks");
            System.out.println("3. Exit");

            System.out.print("\nChoose an option: ");

            // Get the user's menu choice.
            choice = input.nextLine();


            // ========== PROCESSING MENU OPTIONS ==========

            switch (choice) {

                // ========== REGISTER A RISK ==========

                case "1" -> {

                    // Ask the user for risk information.
                    System.out.print("\nRisk name: ");
                    riskName = input.nextLine();

                    System.out.print("Risk description: ");
                    riskDescription = input.nextLine();

                    // We use try to handle invalid number entries.
                    try {

                        System.out.print("Likelihood (1-5): ");
                        likelihood = Integer.parseInt(
                                input.nextLine());

                        System.out.print("Impact (1-5): ");
                        impact = Integer.parseInt(
                                input.nextLine());

                        // Send the information to SQLite.
                        DatabaseManager.addRisk(
                                riskName,
                                riskDescription,
                                likelihood,
                                impact,
                                riskOwnerId
                        );

                    } catch (NumberFormatException e) {

                        // This runs if someone enters letters
                        // instead of a valid whole number.
                        System.out.println(
                                "Please enter numbers for the ratings.");
                    }
                }


                // ========== VIEW REGISTERED RISKS ==========

                case "2" -> {

                    // Get the saved risks from our database.
                    DatabaseManager.viewRisks();
                }


                // ========== EXIT THE SYSTEM ==========

                case "3" -> {

                    // Stop the loop and close BankGuard.
                    running = false;

                    System.out.println(
                            "\nThank you for using BankGuard.");
                }


                // ========== INVALID MENU OPTION ==========

                default -> {

                    // This runs if the user enters
                    // an option that does not exist.
                    System.out.println(
                            "Invalid option. Please try again.");
                }
            }
        }

        // ========== END OF PROGRAM ==========

        // Close Scanner when we finish using it.
        input.close();
    }
}





