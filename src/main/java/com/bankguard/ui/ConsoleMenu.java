
package com.bankguard.ui;

// ============================================================
// BANKGUARD v1.0 - MAIN CONSOLE MENU
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

// I import my models so I can display information
// about employees, risks and audit records.

// I import my services to apply business rules
// before retrieving or changing database information.

// I also use ProjectWorkspaceMenu to manage
// my practical GRC subprojects.

import com.bankguard.model.Risk;
import com.bankguard.model.Employee;
import com.bankguard.model.AuditLog;

import com.bankguard.service.RiskService;
import com.bankguard.service.EmployeeService;
import com.bankguard.service.AuditService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    // I use Scanner to receive keyboard input.
    private final Scanner input;

    // I use these services to manage my bank's
    // risks, employees and audit history.
    private final RiskService riskService;
    private final EmployeeService employeeService;
    private final AuditService auditService;

    // I use this menu for my practical GRC projects.
    private final ProjectWorkspaceMenu projectWorkspaceMenu;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public ConsoleMenu() {

        // I prepare the main Scanner for my application.
        input = new Scanner(System.in);

        // I initialise my risk, employee and audit services.
        riskService = new RiskService();
        employeeService = new EmployeeService();
        auditService = new AuditService();

        // I connect the project workspace to this menu.
        // Both menus share the same Scanner.
        projectWorkspaceMenu = new ProjectWorkspaceMenu(input);
    }

    // ========================================================
    // 3. STARTING MY MAIN MENU
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
                    "\n========== BANKGUARD v1.0 ==========");

            System.out.println("1. Register a new IT risk");
            System.out.println("2. View registered risks");
            System.out.println("3. Update risk status");
            System.out.println("4. Register an employee");
            System.out.println("5. View employees");
            System.out.println("6. Change risk owner");
            System.out.println("7. View risk audit history");

            // I provide access to my GRC subprojects.
            System.out.println("8. GRC Project Workspace");

            System.out.println("9. Exit");

            System.out.print("\nChoose an option: ");
            choice = input.nextLine().trim();

            // ========== MENU SELECTION ==========

            switch (choice) {

                case "1" -> registerRisk();

                case "2" -> viewRisks();

                case "3" -> updateRiskStatus();

                case "4" -> registerEmployee();

                case "5" -> viewEmployees();

                case "6" -> changeRiskOwner();

                case "7" -> viewAuditHistory();

                // I open the GRC Project Workspace.
                case "8" -> projectWorkspaceMenu.start();

                case "9" -> {

                    running = false;

                    System.out.println(
                            "\nThank you for using BankGuard.");
                }

                default -> System.out.println(
                        "Invalid option. Please try again.");
            }
        }

        // ========== END OF PROGRAM ==========

        // I close my Scanner only when the main
        // application is finished.
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

            // I show registered employees so I can
            // select an existing risk owner.
            viewEmployees();

            System.out.print("\nEnter risk owner ID: ");
            ownerId = Integer.parseInt(
                    input.nextLine().trim());

            // I ask my service to validate and
            // save the new risk.
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

            // I retrieve the risks stored in my database.
            risks = riskService.getAllRisks();

            System.out.println(
                    "\n========== IT RISK REGISTER ==========");

            if (risks.isEmpty()) {

                System.out.println(
                        "No risks registered yet.");

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
                        "Likelihood: "
                        + risk.getLikelihood() + "/5");

                System.out.println(
                        "Impact: "
                        + risk.getImpact() + "/5");

                System.out.println(
                        "Risk Score: "
                        + risk.getRiskScore() + "/25");

                System.out.println(
                        "Risk Owner: "
                        + risk.getOwnerName());

                System.out.println(
                        "Status: " + risk.getStatus());
            }

        } catch (SQLException | IOException e) {

            System.out.println(
                    "Error viewing risks: " + e.getMessage());
        }
    }

    // ========================================================
    // 6. UPDATING RISK STATUS WITH AUDIT EVIDENCE
    // ========================================================

    private void updateRiskStatus() {

        // ========== DECLARATIONS ==========

        int riskId;
        int changedBy;
        String statusChoice;
        String riskStatus;
        String result;

        // ========== PROCESSING ==========

        // I show existing risks before selecting
        // which one needs a status update.
        viewRisks();

        try {

            System.out.print(
                    "\nEnter the risk ID number: ");

            riskId = Integer.parseInt(
                    input.nextLine().trim());

            System.out.println("\nAvailable Statuses:");
            System.out.println("1. Open");
            System.out.println("2. In Progress");
            System.out.println("3. Mitigated");
            System.out.println("4. Closed");

            System.out.print("\nChoose the new status: ");
            statusChoice = input.nextLine().trim();

            // ========== VALIDATION ==========

            // I convert my menu choice into a valid status.
            riskStatus = switch (statusChoice) {

                case "1" -> "Open";
                case "2" -> "In Progress";
                case "3" -> "Mitigated";
                case "4" -> "Closed";
                default -> null;
            };

            if (riskStatus == null) {

                System.out.println(
                        "Invalid status selection.");

                return;
            }

            // ========== ACCOUNTABILITY ==========

            // I show employees so the person performing
            // the change can provide their employee ID.
            viewEmployees();

            System.out.print(
                    "\nEnter your employee ID "
                    + "(person making the change): ");

            changedBy = Integer.parseInt(
                    input.nextLine().trim());

            // I send the acting employee ID to my service.
            // My repository saves the status change and
            // audit record in one database transaction.
            result = riskService.changeRiskStatus(
                    riskId,
                    riskStatus,
                    changedBy
            );

            // ========== OUTPUT ==========

            System.out.println(result);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter valid ID numbers.");
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

        System.out.print("\nEmployee name: ");
        name = input.nextLine();

        System.out.print("Employee role: ");
        role = input.nextLine();

        // I validate and save the employee information.
        result = employeeService.registerEmployee(
                name,
                role
        );

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

                System.out.println(
                        "No employees registered.");

                return;
            }

            // ========== OUTPUT ==========

            for (Employee employee : employees) {

                System.out.println(
                        "\n--------------------------------------");

                System.out.println(
                        "Employee ID: "
                        + employee.getEmployeeId());

                System.out.println(
                        "Name: " + employee.getName());

                System.out.println(
                        "Role: " + employee.getRole());
            }

        } catch (SQLException | IOException e) {

            System.out.println(
                    "Error viewing employees: "
                    + e.getMessage());
        }
    }

    // ========================================================
    // 9. CHANGING RISK OWNERSHIP WITH AUDIT EVIDENCE
    // ========================================================

    private void changeRiskOwner() {

        // ========== DECLARATIONS ==========

        int riskId;
        int newOwnerId;
        int changedBy;
        String result;

        // ========== PROCESSING ==========

        // I display risks and employees before
        // selecting which ownership to change.
        viewRisks();
        viewEmployees();

        try {

            System.out.print(
                    "\nEnter the risk ID number: ");

            riskId = Integer.parseInt(
                    input.nextLine().trim());

            System.out.print(
                    "Enter the new risk owner ID: ");

            newOwnerId = Integer.parseInt(
                    input.nextLine().trim());

            // ========== ACCOUNTABILITY ==========

            // I identify the employee reporting
            // that they performed this change.
            System.out.print(
                    "Enter your employee ID "
                    + "(person making the change): ");

            changedBy = Integer.parseInt(
                    input.nextLine().trim());

            // I update the risk owner and create
            // an audit record in the same transaction.
            result = riskService.changeRiskOwner(
                    riskId,
                    newOwnerId,
                    changedBy
            );

            // ========== OUTPUT ==========

            System.out.println(result);

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter valid ID numbers.");
        }
    }

    // ========================================================
    // 10. VIEWING MY RISK AUDIT HISTORY
    // ========================================================

    private void viewAuditHistory() {

        // ========== DECLARATIONS ==========

        int riskId;
        List<AuditLog> auditLogs;

        // ========== PROCESSING ==========

        // I show registered risks before selecting
        // the risk whose history I want to inspect.
        viewRisks();

        try {

            System.out.print(
                    "\nEnter the risk ID to view its history: ");

            riskId = Integer.parseInt(
                    input.nextLine().trim());

            // I retrieve audit records for this risk.
            auditLogs = auditService.getRiskHistory(riskId);

            System.out.println(
                    "\n========== RISK AUDIT HISTORY ==========");

            System.out.println(
                    "Risk ID: R-" + riskId);

            if (auditLogs.isEmpty()) {

                System.out.println(
                        "No audit records found for this risk.");

                return;
            }

            // ========== OUTPUT ==========

            for (AuditLog auditLog : auditLogs) {

                System.out.println(
                        "\n--------------------------------------");

                System.out.println(
                        "Audit ID: "
                        + auditLog.getAuditId());

                System.out.println(
                        "Action: "
                        + auditLog.getAction());

                System.out.println(
                        "Previous Value: "
                        + auditLog.getOldValue());

                System.out.println(
                        "New Value: "
                        + auditLog.getNewValue());

                System.out.println(
                        "Changed By Employee ID: "
                        + auditLog.getChangedBy());

                System.out.println(
                        "Timestamp (UTC): "
                        + auditLog.getChangedAt());
            }

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter a valid risk ID.");

        } catch (SQLException | IOException e) {

            System.out.println(
                    "Error viewing audit history: "
                    + e.getMessage());

        } catch (IllegalArgumentException e) {

            System.out.println(
                    "Invalid audit history request: "
                    + e.getMessage());
        }
    }
}



