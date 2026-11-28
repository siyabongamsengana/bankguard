
package com.bankguard.ui;

// ============================================================
// BANKGUARD v1.2 - MAIN CONSOLE MENU
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

// I import my models to display banking records.
import com.bankguard.model.Risk;
import com.bankguard.model.Employee;

// I use services to apply business rules.
import com.bankguard.service.RiskService;
import com.bankguard.service.EmployeeService;

// I use my database connection to display audit records.
import com.bankguard.database.DatabaseManager;

// I import Java tools for database queries and input.
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ConsoleMenu {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    // I use one Scanner throughout BankGuard.
    private final Scanner input;

    // I use these services for risks and employees.
    private final RiskService riskService;
    private final EmployeeService employeeService;

    // I use this menu to manage GRC projects and tasks.
    private final ProjectWorkspaceMenu projectWorkspaceMenu;

    // I use this menu to manage controls, assessments,
    // evidence and independent assessment reviews.
    private final ControlAssessmentMenu controlAssessmentMenu;

    // I use this menu to manage findings, corrective
    // actions, verification and closure decisions.
    private final FindingsRemediationMenu findingsRemediationMenu;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public ConsoleMenu() {

        // I prepare my console input reader.
        input = new Scanner(System.in);

        // I prepare my existing business services.
        riskService = new RiskService();
        employeeService = new EmployeeService();

        // I reuse the same Scanner in every workspace.
        projectWorkspaceMenu = new ProjectWorkspaceMenu(input);

        controlAssessmentMenu =
                new ControlAssessmentMenu(input);

        findingsRemediationMenu =
                new FindingsRemediationMenu(input);
    }

    // ========================================================
    // 3. STARTING MY BANKGUARD MAIN MENU
    // ========================================================

    public void start() {

        // ========== DECLARATIONS ==========

        boolean running;
        String choice;

        // ========== INITIALISATION ==========

        running = true;

        // ========== PROCESSING ==========

        while (running) {

            displayMainMenu();

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

                case "7" -> viewRiskAuditHistory();

                case "8" -> {

                    // I open my GRC Project Workspace.
                    projectWorkspaceMenu.start();
                }

                case "9" -> {

                    // I open my Control Assessment Workspace.
                    controlAssessmentMenu.start();
                }

                case "10" -> {

                    // I stop my main application loop.
                    running = false;

                    System.out.println(
                            "\nThank you for using BankGuard.");
                }

                case "11" -> {

                    // I open my Findings & Remediation Workspace.
                    //
                    // I can register findings, assign corrective
                    // work, record completion evidence, verify
                    // remediation and close verified findings.
                    findingsRemediationMenu.start();
                }

                default -> System.out.println(
                        "Invalid option. Please try again.");
            }
        }

        // ========== END OF PROGRAM ==========

        // I close my input reader when BankGuard exits.
        input.close();
    }

    // ========================================================
    // 4. DISPLAYING MY MAIN MENU
    // ========================================================

    private void displayMainMenu() {

        System.out.println();

        System.out.println(
                "========== BANKGUARD v1.2 ==========");

        System.out.println("1. Register a new IT risk");
        System.out.println("2. View registered risks");
        System.out.println("3. Update risk status");
        System.out.println("4. Register an employee");
        System.out.println("5. View employees");
        System.out.println("6. Change risk owner");
        System.out.println("7. View risk audit history");
        System.out.println("8. GRC Project Workspace");
        System.out.println("9. Control Assessment Workspace");
        System.out.println("10. Exit");

        // I display the new remediation workspace.
        System.out.println(
                "11. Findings & Remediation Workspace");

        System.out.println(
                "====================================");
    }

    // ========================================================
    // 5. REGISTERING A NEW IT RISK
    // ========================================================

    private void registerRisk() {

        // ========== DECLARATIONS ==========

        String riskName;
        String riskDescription;
        int likelihood;
        int impact;
        int ownerId;
        String result;

        // ========== INPUT ==========

        System.out.println();
        System.out.println("----- REGISTER IT RISK -----");

        System.out.print("Risk name: ");
        riskName = input.nextLine();

        System.out.print("Risk description: ");
        riskDescription = input.nextLine();

        // ========== PROCESSING ==========

        try {

            System.out.print("Likelihood (1-5): ");

            likelihood = Integer.parseInt(
                    input.nextLine().trim());

            System.out.print("Impact (1-5): ");

            impact = Integer.parseInt(
                    input.nextLine().trim());

            // I display employees before choosing an owner.
            viewEmployees();

            System.out.print("\nEnter risk owner ID: ");

            ownerId = Integer.parseInt(
                    input.nextLine().trim());

            // I let my service validate and save the risk.
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
    // 6. VIEWING MY IT RISK REGISTER
    // ========================================================

    private void viewRisks() {

        // ========== DECLARATIONS ==========

        List<Risk> risks;

        // ========== PROCESSING ==========

        try {

            risks = riskService.getAllRisks();

            System.out.println();
            System.out.println(
                    "========== IT RISK REGISTER ==========");

            if (risks.isEmpty()) {

                System.out.println(
                        "No risks registered yet.");

                return;
            }

            // ========== OUTPUT ==========

            for (Risk risk : risks) {

                System.out.println();
                System.out.println(
                        "--------------------------------------");

                System.out.println(
                        "Risk ID: R-" + risk.getRiskId());

                System.out.println(
                        "Risk Name: " + risk.getTitle());

                System.out.println(
                        "Description: "
                        + risk.getDescription());

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
    // 7. UPDATING A RISK STATUS
    // ========================================================

    private void updateRiskStatus() {

        // ========== DECLARATIONS ==========

        int riskId;
        int changedBy;

        String statusChoice;
        String riskStatus;
        String result;

        // ========== PROCESSING ==========

        // I display existing risks before selecting one.
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

            // I display employees to select the person
            // reporting this risk status change.
            viewEmployees();

            System.out.print(
                    "\nEnter acting employee ID: ");

            changedBy = Integer.parseInt(
                    input.nextLine().trim());

            // I let my service update the risk
            // and preserve the audit history.
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
    // 8. REGISTERING AN EMPLOYEE
    // ========================================================

    private void registerEmployee() {

        // ========== DECLARATIONS ==========

        String name;
        String role;
        String result;

        // ========== INPUT ==========

        System.out.println();
        System.out.println("----- REGISTER EMPLOYEE -----");

        System.out.print("Employee name: ");
        name = input.nextLine();

        System.out.print("Employee role: ");
        role = input.nextLine();

        // ========== PROCESSING ==========

        result = employeeService.registerEmployee(
                name,
                role
        );

        // ========== OUTPUT ==========

        System.out.println(result);
    }

    // ========================================================
    // 9. VIEWING REGISTERED EMPLOYEES
    // ========================================================

    private void viewEmployees() {

        // ========== DECLARATIONS ==========

        List<Employee> employees;

        // ========== PROCESSING ==========

        try {

            employees = employeeService.getAllEmployees();

            System.out.println();
            System.out.println(
                    "========== EMPLOYEE REGISTER ==========");

            if (employees.isEmpty()) {

                System.out.println(
                        "No employees registered.");

                return;
            }

            // ========== OUTPUT ==========

            for (Employee employee : employees) {

                System.out.println();
                System.out.println(
                        "--------------------------------------");

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
    // 10. CHANGING A RISK OWNER
    // ========================================================

    private void changeRiskOwner() {

        // ========== DECLARATIONS ==========

        int riskId;
        int newOwnerId;
        int changedBy;

        String result;

        // ========== PROCESSING ==========

        // I display risks and employees so the
        // correct records can be selected.
        viewRisks();
        viewEmployees();

        try {

            System.out.print(
                    "\nEnter the risk ID number: ");

            riskId = Integer.parseInt(
                    input.nextLine().trim());

            System.out.print(
                    "Enter the new owner employee ID: ");

            newOwnerId = Integer.parseInt(
                    input.nextLine().trim());

            System.out.print(
                    "Enter acting employee ID: ");

            changedBy = Integer.parseInt(
                    input.nextLine().trim());

            // I request the ownership transfer.
            // My service handles the database update
            // and the audit record.
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
    // 11. VIEWING RISK AUDIT HISTORY
    // ========================================================

    private void viewRiskAuditHistory() {

        // ========== DECLARATIONS ==========

        int riskId;

        // ========== INPUT ==========

        System.out.println();
        System.out.println(
                "========== RISK AUDIT HISTORY ==========");

        try {

            System.out.print(
                    "Enter the risk ID number: ");

            riskId = Integer.parseInt(
                    input.nextLine().trim());

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter a valid risk ID.");

            return;
        }

        // ========== VALIDATION ==========

        if (riskId <= 0) {

            System.out.println(
                    "Risk ID must be greater than zero.");

            return;
        }

        // ========== PROCESSING ==========

        // I retrieve audit entries for this risk.
        // I use column metadata to display the
        // fields saved in my existing audit table.

        String sql =
                "SELECT * FROM audit_logs "
                + "WHERE risk_id = ? "
                + "ORDER BY rowid DESC";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setInt(1, riskId);

            try (ResultSet results = stmt.executeQuery()) {

                ResultSetMetaData columns =
                        results.getMetaData();

                int columnCount = columns.getColumnCount();
                boolean found = false;

                while (results.next()) {

                    found = true;

                    System.out.println();
                    System.out.println(
                            "--------------------------------------");

                    // I display the stored audit fields
                    // without changing their values.
                    for (int i = 1; i <= columnCount; i++) {

                        String fieldName =
                                columns.getColumnLabel(i);

                        String value =
                                results.getString(i);

                        System.out.println(
                                fieldName + ": " + value);
                    }
                }

                // ========== OUTPUT ==========

                if (!found) {

                    System.out.println(
                            "No audit history found "
                            + "for risk R-" + riskId + ".");
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Unable to retrieve audit history: "
                    + e.getMessage());
        }
    }
}
