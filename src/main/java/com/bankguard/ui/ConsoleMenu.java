
package com.bankguard.ui;

// ============================================================
// IMPORTS
// ============================================================

// I import these classes to interact with employees,
// display risk information and apply my business rules.

import com.bankguard.model.Risk;
import com.bankguard.model.Employee;
import com.bankguard.service.RiskService;
import com.bankguard.service.EmployeeService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private final Scanner input;
    private final RiskService riskService;
    private final EmployeeService employeeService;


    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public ConsoleMenu() {

        // I prepare the tools needed for my menu.
        input = new Scanner(System.in);
        riskService = new RiskService();
        employeeService = new EmployeeService();
    }


    // ========================================================
    // 3. STARTING MY MENU
    // ========================================================

    public void start() {

        // ========== DECLARATIONS ==========

        boolean running;
        String choice;


        // ========== INITIALISATION ==========

        running = true;


        // ========== PROCESSING ==========

        while (running) {

            System.out.println(
                    "\n========== BANKGUARD ==========");

            System.out.println("1. Register a new IT risk");
            System.out.println("2. View registered risks");
            System.out.println("3. Update risk status");
            System.out.println("4. Register an employee");
            System.out.println("5. View employees");
            System.out.println("6. Change risk owner");
            System.out.println("7. Exit");

            System.out.print("\nChoose an option: ");
            choice = input.nextLine();


            // ========== MENU SELECTION ==========

            switch (choice) {

                case "1" -> registerRisk();

                case "2" -> viewRisks();

                case "3" -> updateRiskStatus();

                case "4" -> registerEmployee();

                case "5" -> viewEmployees();

                case "6" -> changeRiskOwner();

                case "7" -> {

                    running = false;

                    System.out.println(
                            "\nThank you for using BankGuard.");
                }

                default -> System.out.println(
                        "Invalid option. Please try again.");
            }
        }


        // ========== END OF PROGRAM ==========

        input.close();
    }


    // ========================================================
    // 4. REGISTERING A NEW RISK
    // ========================================================

    private void registerRisk() {

        // ========== DECLARATIONS ==========

        String riskName;
        String riskDescription;
        int likelihood;
        int impact;
        int ownerId;
        String result;


        // ========== PROCESSING ==========

        System.out.print("\nRisk name: ");
        riskName = input.nextLine();

        System.out.print("Risk description: ");
        riskDescription = input.nextLine();

        try {

            System.out.print("Likelihood (1-5): ");
            likelihood = Integer.parseInt(
                    input.nextLine().trim());

            System.out.print("Impact (1-5): ");
            impact = Integer.parseInt(
                    input.nextLine().trim());

            // I show employees before selecting an owner.
            viewEmployees();

            System.out.print("\nEnter risk owner ID: ");
            ownerId = Integer.parseInt(
                    input.nextLine().trim());


            // I ask my service to validate and save the risk.
            result = riskService.registerRisk(
                    riskName,
                    riskDescription,
                    likelihood,
                    impact,
                    ownerId
            );


            // ========== OUTPUT ==========

            System.out.println(result);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter valid whole numbers.");
        }
    }


    // ========================================================
    // 5. VIEWING REGISTERED RISKS
    // ========================================================

    private void viewRisks() {

        // ========== DECLARATIONS ==========

        List<Risk> risks;


        // ========== PROCESSING ==========

        try {

            risks = riskService.getAllRisks();

            System.out.println(
                    "\n========== IT RISK REGISTER ==========");

            if (risks.isEmpty()) {

                System.out.println("No risks registered yet.");
                return;
            }


            // ========== OUTPUT ==========

            for (Risk risk : risks) {

                System.out.println(
                        "\n--------------------------------------");

                System.out.println(
                        "Risk ID: R-" + risk.getRiskId());

                System.out.println(
                        "Risk Name: " + risk.getTitle());

                System.out.println(
                        "Description: " + risk.getDescription());

                System.out.println(
                        "Likelihood: " + risk.getLikelihood() + "/5");

                System.out.println(
                        "Impact: " + risk.getImpact() + "/5");

                System.out.println(
                        "Risk Score: " + risk.getRiskScore() + "/25");

                System.out.println(
                        "Risk Owner: " + risk.getOwnerName());

                System.out.println(
                        "Status: " + risk.getStatus());
            }

        } catch (SQLException | IOException e) {

            System.out.println(
                    "Error viewing risks: " + e.getMessage());
        }
    }


    // ========================================================
    // 6. UPDATING A RISK STATUS
    // ========================================================

    private void updateRiskStatus() {

        // ========== DECLARATIONS ==========

        int riskId;
        String statusChoice;
        String riskStatus;
        String result;


        // ========== PROCESSING ==========

        viewRisks();

        try {

            System.out.print("\nEnter the risk ID number: ");
            riskId = Integer.parseInt(
                    input.nextLine().trim());

            System.out.println("\nAvailable Statuses:");
            System.out.println("1. Open");
            System.out.println("2. In Progress");
            System.out.println("3. Mitigated");
            System.out.println("4. Closed");

            System.out.print("\nChoose the new status: ");
            statusChoice = input.nextLine();


            // ========== VALIDATION ==========

            riskStatus = switch (statusChoice) {

                case "1" -> "Open";
                case "2" -> "In Progress";
                case "3" -> "Mitigated";
                case "4" -> "Closed";
                default -> null;
            };

            if (riskStatus == null) {

                System.out.println("Invalid status selection.");
                return;
            }


            // ========== OUTPUT ==========

            result = riskService.changeRiskStatus(
                    riskId, riskStatus);

            System.out.println(result);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter a valid risk ID.");
        }
    }


    // ========================================================
    // 7. REGISTERING AN EMPLOYEE
    // ========================================================

    private void registerEmployee() {

        // ========== DECLARATIONS ==========

        String name;
        String role;
        String result;


        // ========== PROCESSING ==========

        // I collect the information of my new employee.
        System.out.print("\nEmployee name: ");
        name = input.nextLine();

        System.out.print("Employee role: ");
        role = input.nextLine();

        // I validate and save the employee.
        result = employeeService.registerEmployee(name, role);


        // ========== OUTPUT ==========

        System.out.println(result);
    }


    // ========================================================
    // 8. VIEWING MY EMPLOYEES
    // ========================================================

    private void viewEmployees() {

        // ========== DECLARATIONS ==========

        List<Employee> employees;


        // ========== PROCESSING ==========

        try {

            employees = employeeService.getAllEmployees();

            System.out.println(
                    "\n========== EMPLOYEE REGISTER ==========");

            if (employees.isEmpty()) {

                System.out.println("No employees registered.");
                return;
            }


            // ========== OUTPUT ==========

            for (Employee employee : employees) {

                System.out.println(
                        "\n--------------------------------------");

                System.out.println(
                        "Employee ID: " + employee.getEmployeeId());

                System.out.println(
                        "Name: " + employee.getName());

                System.out.println(
                        "Role: " + employee.getRole());
            }

        } catch (SQLException | IOException e) {

            System.out.println(
                    "Error viewing employees: " + e.getMessage());
        }
    }


    // ========================================================
    // 9. CHANGING MY RISK OWNER
    // ========================================================

    private void changeRiskOwner() {

        // ========== DECLARATIONS ==========

        int riskId;
        int employeeId;
        String result;


        // ========== PROCESSING ==========

        // I display existing risks and employees
        // so I can choose the correct records.
        viewRisks();
        viewEmployees();

        try {

            System.out.print(
                    "\nEnter the risk ID number: ");

            riskId = Integer.parseInt(
                    input.nextLine().trim());

            System.out.print(
                    "Enter the new employee ID: ");

            employeeId = Integer.parseInt(
                    input.nextLine().trim());


            // I request the ownership change.
            result = riskService.changeRiskOwner(
                    riskId, employeeId);


            // ========== OUTPUT ==========

            System.out.println(result);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter valid ID numbers.");
        }
    }
}
