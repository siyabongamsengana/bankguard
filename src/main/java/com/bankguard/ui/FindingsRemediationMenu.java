
package com.bankguard.ui;

// ============================================================
// BANKGUARD v1.2 - FINDINGS AND REMEDIATION WORKSPACE
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

// I import my models to display saved records.
import com.bankguard.model.Finding;
import com.bankguard.model.RemediationAction;
import com.bankguard.model.RemediationVerification;

// I import my services to apply GRC business rules.
import com.bankguard.service.FindingService;
import com.bankguard.service.RemediationActionService;
import com.bankguard.service.RemediationVerificationService;
import com.bankguard.service.FindingClosureService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class FindingsRemediationMenu {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private final Scanner input;

    private final FindingService findingService;
    private final RemediationActionService actionService;
    private final RemediationVerificationService verificationService;
    private final FindingClosureService closureService;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public FindingsRemediationMenu(Scanner input) {

        // I reuse the Scanner from the main menu.
        this.input = input;

        // I prepare my GRC services.
        findingService = new FindingService();
        actionService = new RemediationActionService();

        verificationService =
                new RemediationVerificationService();

        closureService = new FindingClosureService();
    }

    // ========================================================
    // 3. STARTING MY WORKSPACE
    // ========================================================

    public void start() {

        // ========== DECLARATIONS ==========

        boolean running;
        String choice;

        // ========== INITIALISATION ==========

        running = true;

        // ========== PROCESSING ==========

        while (running) {

            displayMenu();

            choice = readText("Choose an option: ");

            switch (choice) {

                case "1" -> registerFinding();

                case "2" -> viewFindings();

                case "3" -> registerRemediationAction();

                case "4" -> viewRemediationActions();

                case "5" -> completeRemediationAction();

                case "6" -> verifyRemediation();

                case "7" -> viewVerificationHistory();

                case "8" -> closeFinding();

                case "9" -> {

                    System.out.println(
                            "Returning to main menu...");

                    running = false;
                }

                default -> System.out.println(
                        "Invalid option. Please try again.");
            }
        }
    }

    // ========================================================
    // 4. DISPLAYING MY MENU
    // ========================================================

    private void displayMenu() {

        System.out.println();
        System.out.println(
                "========== FINDINGS & REMEDIATION ==========");

        System.out.println("1. Register a finding");
        System.out.println("2. View all findings");
        System.out.println("3. Register a remediation action");
        System.out.println("4. View remediation actions");
        System.out.println("5. Complete a remediation action");
        System.out.println("6. Independently verify remediation");
        System.out.println("7. View verification history");
        System.out.println("8. Close a verified finding");
        System.out.println("9. Return to main menu");

        System.out.println(
                "============================================");
    }

    // ========================================================
    // 5. REGISTERING A FINDING
    // ========================================================

    private void registerFinding() {

        // ========== DECLARATIONS ==========

        int testId;
        int ownerId;

        String title;
        String description;
        String severity;
        String dueDate;
        String result;

        // ========== INPUT ==========

        System.out.println();
        System.out.println("----- REGISTER FINDING -----");

        testId = readPositiveInt(
                "Enter existing control assessment ID: ");

        if (testId <= 0) {
            return;
        }

        title = readText("Finding title: ");

        description = readText(
                "Describe the control weakness: ");

        System.out.println();
        System.out.println("Severity levels:");
        System.out.println("1. Low");
        System.out.println("2. Medium");
        System.out.println("3. High");
        System.out.println("4. Critical");

        severity = readText("Select severity: ");

        // ========== PROCESSING ==========

        switch (severity) {

            case "1" -> severity = "Low";
            case "2" -> severity = "Medium";
            case "3" -> severity = "High";
            case "4" -> severity = "Critical";

            default -> {
                // My service validates typed names.
            }
        }

        ownerId = readPositiveInt(
                "Enter finding owner employee ID: ");

        if (ownerId <= 0) {
            return;
        }

        dueDate = readText(
                "Remediation deadline (YYYY-MM-DD): ");

        // I save the finding through my service.
        result = findingService.registerFinding(
                testId,
                title,
                description,
                severity,
                ownerId,
                dueDate
        );

        // ========== OUTPUT ==========

        System.out.println();
        System.out.println(result);
    }

    // ========================================================
    // 6. VIEWING ALL FINDINGS
    // ========================================================

    private void viewFindings() {

        System.out.println();
        System.out.println("----- FINDINGS REGISTER -----");

        try {

            List<Finding> findings =
                    findingService.getAllFindings();

            if (findings.isEmpty()) {

                System.out.println(
                        "No findings have been registered.");

                return;
            }

            for (Finding finding : findings) {

                System.out.println();
                System.out.println(
                        "----------------------------------------");

                System.out.println(
                        "Finding ID: " + finding.getFindingId());

                System.out.println(
                        "Assessment ID: " + finding.getTestId());

                System.out.println(
                        "Title: " + finding.getFindingTitle());

                System.out.println(
                        "Description: "
                        + finding.getFindingDescription());

                System.out.println(
                        "Severity: " + finding.getSeverity());

                System.out.println(
                        "Owner Employee ID: " + finding.getOwnerId());

                System.out.println(
                        "Deadline: " + finding.getDueDate());

                System.out.println(
                        "Status: " + finding.getStatus());

                System.out.println(
                        "Created: " + finding.getCreatedAt());
            }

        } catch (SQLException | IOException e) {

            System.out.println(
                    "Unable to retrieve findings: "
                    + e.getMessage());
        }
    }

    // ========================================================
    // 7. REGISTERING A REMEDIATION ACTION
    // ========================================================

    private void registerRemediationAction() {

        // ========== DECLARATIONS ==========

        int findingId;
        int assignedTo;

        String description;
        String dueDate;
        String result;

        // ========== INPUT ==========

        System.out.println();
        System.out.println(
                "----- REGISTER REMEDIATION ACTION -----");

        findingId = readPositiveInt("Enter finding ID: ");

        if (findingId <= 0) {
            return;
        }

        description = readText(
                "Describe the corrective action: ");

        assignedTo = readPositiveInt(
                "Enter assigned employee ID: ");

        if (assignedTo <= 0) {
            return;
        }

        dueDate = readText(
                "Action deadline (YYYY-MM-DD): ");

        // ========== PROCESSING ==========

        result = actionService.registerAction(
                findingId,
                description,
                assignedTo,
                dueDate
        );

        // ========== OUTPUT ==========

        System.out.println();
        System.out.println(result);
    }

    // ========================================================
    // 8. VIEWING REMEDIATION ACTIONS
    // ========================================================

    private void viewRemediationActions() {

        // ========== DECLARATIONS ==========

        int findingId;

        // ========== INPUT ==========

        System.out.println();
        System.out.println(
                "----- VIEW REMEDIATION ACTIONS -----");

        findingId = readPositiveInt("Enter finding ID: ");

        if (findingId <= 0) {
            return;
        }

        // ========== PROCESSING ==========

        try {

            List<RemediationAction> actions =
                    actionService.getActionsByFinding(findingId);

            if (actions.isEmpty()) {

                System.out.println(
                        "No remediation actions found.");

                return;
            }

            // ========== OUTPUT ==========

            for (RemediationAction action : actions) {

                System.out.println();
                System.out.println(
                        "----------------------------------------");

                System.out.println(
                        "Action ID: " + action.getActionId());

                System.out.println(
                        "Finding ID: " + action.getFindingId());

                System.out.println(
                        "Corrective Action: "
                        + action.getActionDescription());

                System.out.println(
                        "Assigned Employee ID: "
                        + action.getAssignedTo());

                System.out.println(
                        "Deadline: " + action.getDueDate());

                System.out.println(
                        "Status: " + action.getStatus());

                System.out.println(
                        "Completion Evidence: "
                        + action.getCompletionEvidence());

                System.out.println(
                        "Created: " + action.getCreatedAt());
            }

        } catch (SQLException | IOException e) {

            System.out.println(
                    "Unable to retrieve actions: "
                    + e.getMessage());
        }
    }

    // ========================================================
    // 9. COMPLETING A REMEDIATION ACTION
    // ========================================================

    private void completeRemediationAction() {

        // ========== DECLARATIONS ==========

        int actionId;
        int employeeId;

        String evidenceReference;
        String result;

        // ========== INPUT ==========

        System.out.println();
        System.out.println(
                "----- COMPLETE REMEDIATION ACTION -----");

        actionId = readPositiveInt("Enter action ID: ");

        if (actionId <= 0) {
            return;
        }

        employeeId = readPositiveInt(
                "Enter assigned employee ID: ");

        if (employeeId <= 0) {
            return;
        }

        evidenceReference = readText(
                "Completion evidence reference: ");

        // ========== PROCESSING ==========

        result = actionService.completeAction(
                actionId,
                employeeId,
                evidenceReference
        );

        // ========== OUTPUT ==========

        System.out.println();
        System.out.println(result);
    }

    // ========================================================
    // 10. INDEPENDENTLY VERIFYING REMEDIATION
    // ========================================================

    private void verifyRemediation() {

        // ========== DECLARATIONS ==========

        int findingId;
        int reviewerId;

        String decision;
        String evidenceAssessment;
        String notes;
        String result;

        // ========== INPUT ==========

        System.out.println();
        System.out.println(
                "----- INDEPENDENT REMEDIATION VERIFICATION -----");

        findingId = readPositiveInt("Enter finding ID: ");

        if (findingId <= 0) {
            return;
        }

        reviewerId = readPositiveInt(
                "Enter independent reviewer employee ID: ");

        if (reviewerId <= 0) {
            return;
        }

        System.out.println();
        System.out.println("Verification decisions:");
        System.out.println("1. Verified");
        System.out.println("2. Rejected");

        decision = readText("Select decision: ");

        switch (decision) {

            case "1" -> decision = "Verified";
            case "2" -> decision = "Rejected";

            default -> {
                // My service validates typed decisions.
            }
        }

        evidenceAssessment = readText(
                "Describe your evidence assessment: ");

        notes = readText(
                "Explain your verification decision: ");

        // ========== PROCESSING ==========

        result = verificationService.verifyFinding(
                findingId,
                reviewerId,
                decision,
                evidenceAssessment,
                notes
        );

        // ========== OUTPUT ==========

        System.out.println();
        System.out.println(result);
    }

    // ========================================================
    // 11. VIEWING VERIFICATION HISTORY
    // ========================================================

    private void viewVerificationHistory() {

        // ========== DECLARATIONS ==========

        int findingId;

        // ========== INPUT ==========

        System.out.println();
        System.out.println(
                "----- REMEDIATION VERIFICATION HISTORY -----");

        findingId = readPositiveInt("Enter finding ID: ");

        if (findingId <= 0) {
            return;
        }

        // ========== PROCESSING ==========

        try {

            List<RemediationVerification> history =
                    verificationService.getVerificationHistory(
                            findingId);

            if (history.isEmpty()) {

                System.out.println(
                        "No verification decisions recorded.");

                return;
            }

            // ========== OUTPUT ==========

            for (RemediationVerification verification : history) {

                System.out.println();
                System.out.println(
                        "----------------------------------------");

                System.out.println(
                        "Verification ID: "
                        + verification.getVerificationId());

                System.out.println(
                        "Finding ID: "
                        + verification.getFindingId());

                System.out.println(
                        "Reviewer Employee ID: "
                        + verification.getReviewerId());

                System.out.println(
                        "Decision: " + verification.getDecision());

                System.out.println(
                        "Evidence Assessment: "
                        + verification.getEvidenceAssessment());

                System.out.println(
                        "Review Notes: "
                        + verification.getVerificationNotes());

                System.out.println(
                        "Reviewed At: "
                        + verification.getVerifiedAt());
            }

        } catch (SQLException | IOException e) {

            System.out.println(
                    "Unable to retrieve verification history: "
                    + e.getMessage());
        }
    }

    // ========================================================
    // 12. CLOSING A VERIFIED FINDING
    // ========================================================

    private void closeFinding() {

        // ========== DECLARATIONS ==========

        int findingId;
        int employeeId;

        String notes;
        String confirmation;
        String result;

        // ========== INPUT ==========

        System.out.println();
        System.out.println(
                "----- FORMAL FINDING CLOSURE -----");

        findingId = readPositiveInt("Enter finding ID: ");

        if (findingId <= 0) {
            return;
        }

        employeeId = readPositiveInt(
                "Enter closure employee ID: ");

        if (employeeId <= 0) {
            return;
        }

        notes = readText(
                "Explain why this finding can be closed: ");

        // I ask for confirmation before saving
        // the formal closure decision.
        confirmation = readText(
                "Confirm closure? Type YES: ");

        if (!"YES".equals(confirmation)) {

            System.out.println(
                    "Finding closure cancelled.");

            return;
        }

        // ========== PROCESSING ==========

        // My service checks the remediation actions,
        // evidence references and verification decision.
        result = closureService.closeFinding(
                findingId,
                employeeId,
                notes
        );

        // ========== OUTPUT ==========

        System.out.println();
        System.out.println(result);
    }

    // ========================================================
    // 13. READING POSITIVE INTEGERS
    // ========================================================

    private int readPositiveInt(String message) {

        // ========== DECLARATIONS ==========

        String value;
        int number;

        // ========== INPUT ==========

        System.out.print(message);
        value = input.nextLine().trim();

        // ========== VALIDATION ==========

        try {

            number = Integer.parseInt(value);

            if (number <= 0) {

                System.out.println(
                        "Enter a number greater than zero.");

                return -1;
            }

            return number;

        } catch (NumberFormatException e) {

            System.out.println(
                    "Please enter a valid whole number.");

            return -1;
        }
    }

    // ========================================================
    // 14. READING TEXT INPUT
    // ========================================================

    private String readText(String message) {

        System.out.print(message);

        return input.nextLine().trim();
    }
}
