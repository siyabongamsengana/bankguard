
package com.bankguard.ui;

// ============================================================
// IMPORTS
// ============================================================

// I import my models so I can display information
// about risks, employees and audit records.

// I import my services to apply my business rules
// before information is saved or retrieved.

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

    // I use Scanner to receive information from the user.
    private final Scanner input;

    // I use these services to manage my bank's
    // risks, employees and audit records.
    private final RiskService riskService;
    private final EmployeeService employeeService;
    private final AuditService auditService;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public ConsoleMenu() {

        // I prepare the tools required by my application.
        input = new Scanner(System.in);

        riskService = new RiskService();
        employeeService = new EmployeeService();

        // I initialise my audit service for v0.9.
        auditService = new AuditService();
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

            // I add an option to review changes made
            // to risks in my fictional bank.
            System.out.println("7. View risk audit history");

            System.out.println("8. Exit");

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

                case "8" -> {

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

            // I display registered employees before
            // asking who will own this new risk.
            viewEmployees();

            System.out.print("\nEnter risk owner ID: ");
            ownerId = Integer.parseInt(
                    input.nextLine().trim());

            // I ask my service to validate and save
            // the risk in my SQLite database.
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
                        "Likelihood: "
                        + risk.getLikelihood() + "/5");

                System.out.println(
                        "Impact: "
                        + risk.getImpact() + "/5");

                System.out.println(
                        "Risk Score: "
                        + risk.getRiskScore() + "/25");

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
    // 6. UPDATING A RISK STATUS WITH AUDIT EVIDENCE
    // ========================================================

    private void updateRiskStatus() {

        // ========== DECLARATIONS ==========

        int riskId;
        int changedBy;
        String statusChoice;
        String riskStatus;
        String result;

        // ========== PROCESSING ==========

        // I display my registered risks so I can
        // identify the correct record to update.
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
            statusChoice = input.nextLine().trim();

            // ========== VALIDATION ==========

            // I convert the menu choice into a
            // status supported by my database.
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

            // I display employees so the person performing
            // the change can supply their employee ID.
            viewEmployees();

            System.out.print(
                    "\nEnter your employee ID "
                    + "(person making the change): ");

            changedBy = Integer.parseInt(
                    input.nextLine().trim());

            // I send the acting employee ID to my service.
            // My service validates the ID, and my repository
            // records the status change in the audit trail.
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

        // I validate and save the new employee.
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

        // I display existing risks and employees
        // so I can select the correct records.
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

            // I ask which employee is reporting
            // that they performed this change.
            System.out.print(
                    "Enter your employee ID "
                    + "(person making the change): ");

            changedBy = Integer.parseInt(
                    input.nextLine().trim());

            // I pass all three IDs to my risk service.
            // My repository will update the owner and
            // create an audit record in one transaction.
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
    // 10. VIEWING A RISK'S AUDIT HISTORY - v0.9
    // ========================================================

    private void viewAuditHistory() {

        // ========== DECLARATIONS ==========

        int riskId;
        List<AuditLog> auditLogs;

        // ========== PROCESSING ==========

        // I display my risks before asking which
        // audit history the user wants to review.
        viewRisks();

        try {

            System.out.print(
                    "\nEnter the risk ID to view its history: ");

            riskId = Integer.parseInt(
                    input.nextLine().trim());

            // I retrieve all recorded changes for this risk.
            auditLogs = auditService.getRiskHistory(riskId);

            System.out.println(
                    "\n========== RISK AUDIT HISTORY ==========");

            System.out.println("Risk ID: R-" + riskId);

            // I explain when no audit records exist.
            // My system only records supported changes
            // made after audit logging was introduced.
            if (auditLogs.isEmpty()) {

                System.out.println(
                        "No audit records found for this risk.");

                return;
            }

            // ========== OUTPUT ==========

            // I display each recorded change.
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


