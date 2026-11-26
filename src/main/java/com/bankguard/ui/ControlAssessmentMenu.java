
package com.bankguard.ui;

// ============================================================
// BANKGUARD v1.1 - CONTROL ASSESSMENT WORKSPACE
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

// I import my control, test and evidence models
// so I can display records saved in SQLite.

import com.bankguard.model.Control;
import com.bankguard.model.ControlTest;
import com.bankguard.model.Evidence;

// I use my services to apply validation and
// retrieve or save assessment information.

import com.bankguard.service.ControlService;
import com.bankguard.service.ControlTestService;
import com.bankguard.service.EvidenceService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ControlAssessmentMenu {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    // I reuse the Scanner from my main BankGuard menu.
    private final Scanner input;

    // I use these services to manage my controls,
    // assessments and evidence references.
    private final ControlService controlService;
    private final ControlTestService testService;
    private final EvidenceService evidenceService;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public ControlAssessmentMenu(Scanner input) {

        // I share the same input reader with my main menu.
        this.input = input;

        // I prepare the services for this workspace.
        this.controlService = new ControlService();
        this.testService = new ControlTestService();
        this.evidenceService = new EvidenceService();
    }

    // ========================================================
    // 3. STARTING MY CONTROL ASSESSMENT WORKSPACE
    // ========================================================

    public void start() {

        // ========== DECLARATIONS ==========

        int choice;
        boolean running;

        // ========== INITIALISATION ==========

        running = true;

        // ========== PROCESSING ==========

        while (running) {

            displayMenu();

            choice = readPositiveInt("Choose an option: ");

            switch (choice) {

                case 1 ->
                    registerControl();

                case 2 ->
                    viewControlRegister();

                case 3 ->
                    viewControlsByRisk();

                case 4 ->
                    recordControlAssessment();

                case 5 ->
                    viewTestingHistory();

                case 6 ->
                    registerEvidence();

                case 7 ->
                    viewAssessmentEvidence();

                case 8 -> {

                    // I return to BankGuard's main menu.
                    System.out.println(
                            "Returning to the main menu...");

                    running = false;
                }

                default ->
                    System.out.println(
                            "Invalid option. Please try again.");
            }
        }
    }

    // ========================================================
    // 4. DISPLAYING MY WORKSPACE MENU
    // ========================================================

    private void displayMenu() {

        System.out.println();
        System.out.println(
                "========== CONTROL ASSESSMENT WORKSPACE ==========");
        System.out.println("1. Register an IT control");
        System.out.println("2. View control register");
        System.out.println("3. View controls linked to a risk");
        System.out.println("4. Record a control assessment");
        System.out.println("5. View control testing history");
        System.out.println("6. Register evidence");
        System.out.println("7. View assessment evidence");
        System.out.println("8. Return to main menu");
        System.out.println("==================================================");
    }

    // ========================================================
    // 5. REGISTERING AN IT CONTROL
    // ========================================================

    private void registerControl() {

        // ========== DECLARATIONS ==========

        int riskId;
        int ownerId;
        String controlName;
        String objective;
        String type;
        String frequency;
        String result;

        // ========== INPUT ==========

        System.out.println();
        System.out.println("----- REGISTER IT CONTROL -----");

        riskId = readPositiveInt("Enter risk ID: ");

        if (riskId <= 0) {
            return;
        }

        controlName = readText("Control name: ");
        objective = readText("Control objective: ");

        System.out.println();
        System.out.println("Control types:");
        System.out.println("1. Preventive");
        System.out.println("2. Detective");
        System.out.println("3. Corrective");

        type = readText(
                "Enter type name or number: ");

        // ========== PROCESSING ==========

        // I convert numbered choices to the exact
        // control type values accepted by SQLite.
        switch (type) {

            case "1" -> type = "Preventive";
            case "2" -> type = "Detective";
            case "3" -> type = "Corrective";
            default -> {
                // My service will validate a typed name.
            }
        }

        ownerId = readPositiveInt(
                "Enter registered employee owner ID: ");

        if (ownerId <= 0) {
            return;
        }

        frequency = readText(
                "Control frequency (e.g. Every login): ");

        // I let my service validate the information
        // and save the control if the details are valid.
        result = controlService.registerControl(
                riskId,
                controlName,
                objective,
                type,
                ownerId,
                frequency
        );

        // ========== OUTPUT ==========

        System.out.println();
        System.out.println(result);
    }

    // ========================================================
    // 6. VIEWING THE FULL CONTROL REGISTER
    // ========================================================

    private void viewControlRegister() {

        System.out.println();
        System.out.println("----- CONTROL REGISTER -----");

        try {

            // I retrieve all registered controls.
            List<Control> controls =
                    controlService.getAllControls();

            if (controls.isEmpty()) {

                System.out.println(
                        "No controls have been registered.");

                return;
            }

            // I display every saved control.
            displayControls(controls);

        } catch (SQLException | IOException e) {

            System.out.println(
                    "Unable to load control register: "
                    + e.getMessage());
        }
    }

    // ========================================================
    // 7. VIEWING CONTROLS LINKED TO ONE RISK
    // ========================================================

    private void viewControlsByRisk() {

        // ========== DECLARATIONS ==========

        int riskId;

        // ========== INPUT ==========

        System.out.println();
        System.out.println(
                "----- VIEW CONTROLS BY RISK -----");

        riskId = readPositiveInt("Enter risk ID: ");

        if (riskId <= 0) {
            return;
        }

        // ========== PROCESSING ==========

        try {

            List<Control> controls =
                    controlService.getControlsByRisk(riskId);

            // ========== OUTPUT ==========

            if (controls.isEmpty()) {

                System.out.println(
                        "No controls found for risk "
                        + riskId + ".");

                return;
            }

            displayControls(controls);

        } catch (SQLException | IOException e) {

            System.out.println(
                    "Unable to retrieve controls: "
                    + e.getMessage());
        }
    }

    // ========================================================
    // 8. DISPLAYING CONTROL DETAILS
    // ========================================================

    private void displayControls(List<Control> controls) {

        // I display enough information to understand
        // the control and who is responsible for it.

        for (Control control : controls) {

            System.out.println();
            System.out.println(
                    "----------------------------------------");

            System.out.println(
                    "Control ID: "
                    + control.getControlId());

            System.out.println(
                    "Risk ID: "
                    + control.getRiskId());

            System.out.println(
                    "Name: "
                    + control.getControlName());

            System.out.println(
                    "Objective: "
                    + control.getControlObjective());

            System.out.println(
                    "Type: "
                    + control.getControlType());

            System.out.println(
                    "Owner Employee ID: "
                    + control.getOwnerId());

            System.out.println(
                    "Frequency: "
                    + control.getFrequency());

            System.out.println(
                    "Status: "
                    + control.getStatus());

            System.out.println(
                    "Created: "
                    + control.getCreatedAt());
        }

        System.out.println(
                "----------------------------------------");
    }

    // ========================================================
    // 9. RECORDING A CONTROL ASSESSMENT
    // ========================================================

    private void recordControlAssessment() {

        // ========== DECLARATIONS ==========

        int controlId;
        int testerId;
        String procedure;
        String period;
        String observations;
        String conclusion;
        String result;

        // ========== INPUT ==========

        System.out.println();
        System.out.println(
                "----- RECORD CONTROL ASSESSMENT -----");

        controlId = readPositiveInt("Enter control ID: ");

        if (controlId <= 0) {
            return;
        }

        procedure = readText(
                "Describe the test procedure: ");

        period = readText(
                "Assessment period (e.g. November 2026): ");

        observations = readText(
                "Test observations or results: ");

        System.out.println();
        System.out.println("Assessment conclusions:");
        System.out.println("1. Not Tested");
        System.out.println("2. Effective");
        System.out.println("3. Ineffective");
        System.out.println("4. Inconclusive");

        conclusion = readText(
                "Enter conclusion name or number: ");

        // ========== PROCESSING ==========

        switch (conclusion) {

            case "1" -> conclusion = "Not Tested";
            case "2" -> conclusion = "Effective";
            case "3" -> conclusion = "Ineffective";
            case "4" -> conclusion = "Inconclusive";
            default -> {
                // My service will validate a typed name.
            }
        }

        testerId = readPositiveInt(
                "Enter registered tester employee ID: ");

        if (testerId <= 0) {
            return;
        }

        // I save the assessment using my service.
        result = testService.registerControlTest(
                controlId,
                procedure,
                period,
                observations,
                conclusion,
                testerId
        );

        // ========== OUTPUT ==========

        System.out.println();
        System.out.println(result);

        // I remind myself that the recorded conclusion
        // has not been independently verified.
        if (result.startsWith(
                "Control test recorded successfully")) {

            System.out.println(
                    "Note: This is a recorded assessment, "
                    + "not an approved audit conclusion.");

            System.out.println(
                    "Use option 5 to find the test ID, "
                    + "then option 6 to register its evidence.");
        }
    }

    // ========================================================
    // 10. VIEWING CONTROL TESTING HISTORY
    // ========================================================

    private void viewTestingHistory() {

        // ========== DECLARATIONS ==========

        int controlId;

        // ========== INPUT ==========

        System.out.println();
        System.out.println(
                "----- CONTROL TESTING HISTORY -----");

        controlId = readPositiveInt("Enter control ID: ");

        if (controlId <= 0) {
            return;
        }

        // ========== PROCESSING ==========

        try {

            List<ControlTest> tests =
                    testService.getTestsByControl(controlId);

            // ========== OUTPUT ==========

            if (tests.isEmpty()) {

                System.out.println(
                        "No assessments found for control "
                        + controlId + ".");

                return;
            }

            for (ControlTest test : tests) {

                System.out.println();
                System.out.println(
                        "----------------------------------------");

                System.out.println(
                        "Test ID: " + test.getTestId());

                System.out.println(
                        "Control ID: " + test.getControlId());

                System.out.println(
                        "Procedure: " + test.getTestProcedure());

                System.out.println(
                        "Assessment Period: "
                        + test.getAssessmentPeriod());

                System.out.println(
                        "Observations: "
                        + test.getTestResults());

                System.out.println(
                        "Recorded Conclusion: "
                        + test.getConclusion());

                System.out.println(
                        "Tester Employee ID: "
                        + test.getTestedBy());

                System.out.println(
                        "Test Recorded At: "
                        + test.getTestedAt());
            }

            System.out.println(
                    "----------------------------------------");

            System.out.println(
                    "Note: Conclusions are not yet "
                    + "subject to an approval workflow.");

        } catch (SQLException | IOException e) {

            System.out.println(
                    "Unable to load testing history: "
                    + e.getMessage());
        }
    }

    // ========================================================
    // 11. REGISTERING AN EVIDENCE REFERENCE
    // ========================================================

    private void registerEvidence() {

        // ========== DECLARATIONS ==========

        int testId;
        String evidenceName;
        String source;
        String description;
        String reference;
        String result;

        // ========== INPUT ==========

        System.out.println();
        System.out.println(
                "----- REGISTER ASSESSMENT EVIDENCE -----");

        testId = readPositiveInt("Enter control test ID: ");

        if (testId <= 0) {
            return;
        }

        evidenceName = readText("Evidence name: ");

        source = readText(
                "Evidence source (e.g. Security Team): ");

        description = readText(
                "Describe what the evidence shows: ");

        reference = readText(
                "Evidence reference or document ID: ");

        // ========== PROCESSING ==========

        // I register a description and reference.
        // The actual evidence file is not uploaded.
        result = evidenceService.registerEvidence(
                testId,
                evidenceName,
                source,
                description,
                reference
        );

        // ========== OUTPUT ==========

        System.out.println();
        System.out.println(result);
    }

    // ========================================================
    // 12. VIEWING EVIDENCE LINKED TO AN ASSESSMENT
    // ========================================================

    private void viewAssessmentEvidence() {

        // ========== DECLARATIONS ==========

        int testId;

        // ========== INPUT ==========

        System.out.println();
        System.out.println(
                "----- VIEW ASSESSMENT EVIDENCE -----");

        testId = readPositiveInt("Enter control test ID: ");

        if (testId <= 0) {
            return;
        }

        // ========== PROCESSING ==========

        try {

            List<Evidence> records =
                    evidenceService.getEvidenceByTest(testId);

            // ========== OUTPUT ==========

            if (records.isEmpty()) {

                System.out.println(
                        "No evidence references found "
                        + "for test " + testId + ".");

                return;
            }

            for (Evidence evidence : records) {

                System.out.println();
                System.out.println(
                        "----------------------------------------");

                System.out.println(
                        "Evidence ID: "
                        + evidence.getEvidenceId());

                System.out.println(
                        "Control Test ID: "
                        + evidence.getTestId());

                System.out.println(
                        "Evidence Name: "
                        + evidence.getEvidenceName());

                System.out.println(
                        "Source: "
                        + evidence.getEvidenceSource());

                System.out.println(
                        "Description: "
                        + evidence.getEvidenceDescription());

                System.out.println(
                        "Reference: "
                        + evidence.getEvidenceReference());

                System.out.println(
                        "Recorded At: "
                        + evidence.getRecordedAt());
            }

            System.out.println(
                    "----------------------------------------");

            System.out.println(
                    "These are evidence references. "
                    + "The underlying files have not "
                    + "been uploaded or verified.");

        } catch (SQLException | IOException e) {

            System.out.println(
                    "Unable to retrieve evidence: "
                    + e.getMessage());
        }
    }

    // ========================================================
    // 13. READING NUMERIC INPUT SAFELY
    // ========================================================

    private int readPositiveInt(String message) {

        // ========== DECLARATIONS ==========

        String enteredValue;
        int number;

        // ========== INPUT ==========

        System.out.print(message);

        // I read the whole line to avoid Scanner
        // problems caused by mixing nextInt and nextLine.
        enteredValue = input.nextLine().trim();

        // ========== VALIDATION ==========

        try {

            number = Integer.parseInt(enteredValue);

            if (number <= 0) {

                System.out.println(
                        "Please enter a number greater "
                        + "than zero.");

                return -1;
            }

            // ========== OUTPUT ==========

            return number;

        } catch (NumberFormatException e) {

            System.out.println(
                    "Invalid number. Please enter "
                    + "a whole number.");

            return -1;
        }
    }

    // ========================================================
    // 14. READING TEXT INPUT
    // ========================================================

    private String readText(String message) {

        // I read the user's text without changing
        // its meaning. My services validate it.
        System.out.print(message);

        return input.nextLine().trim();
    }
}
