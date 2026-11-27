
package com.bankguard.ui;

// ============================================================
// BANKGUARD - CONTROL ASSESSMENT & REVIEW WORKSPACE
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

// I import the models needed to display saved records.
import com.bankguard.model.Control;
import com.bankguard.model.ControlTest;
import com.bankguard.model.Evidence;
import com.bankguard.model.AssessmentReview;

// I use my services to apply business rules.
import com.bankguard.service.ControlService;
import com.bankguard.service.ControlTestService;
import com.bankguard.service.EvidenceService;
import com.bankguard.service.AssessmentReviewService;

// I use these classes for console input and database errors.
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ControlAssessmentMenu {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private final Scanner input;

    private final ControlService controlService;
    private final ControlTestService testService;
    private final EvidenceService evidenceService;
    private final AssessmentReviewService reviewService;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public ControlAssessmentMenu(Scanner input) {

        // I reuse the Scanner from my main menu.
        this.input = input;

        // I initialise my assessment services.
        controlService = new ControlService();
        testService = new ControlTestService();
        evidenceService = new EvidenceService();

        // I initialise the new review service.
        reviewService = new AssessmentReviewService();
    }

    // ========================================================
    // 3. STARTING MY WORKSPACE
    // ========================================================

    public void start() {

        // ========== DECLARATIONS ==========

        boolean running;
        int choice;

        // ========== INITIALISATION ==========

        running = true;

        // ========== PROCESSING ==========

        while (running) {

            displayMenu();

            choice = readPositiveInt("Choose an option: ");

            switch (choice) {

                case 1 -> registerControl();

                case 2 -> viewControlRegister();

                case 3 -> viewControlsByRisk();

                case 4 -> recordControlAssessment();

                case 5 -> viewTestingHistory();

                case 6 -> registerEvidence();

                case 7 -> viewAssessmentEvidence();

                case 8 -> reviewAssessment();

                case 9 -> viewAssessmentReview();

                case 10 -> {

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
        System.out.println("8. Review an assessment");
        System.out.println("9. View assessment review");
        System.out.println("10. Return to main menu");

        System.out.println(
                "==================================================");
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

        type = readText("Enter type name or number: ");

        // ========== PROCESSING ==========

        switch (type) {

            case "1" -> type = "Preventive";
            case "2" -> type = "Detective";
            case "3" -> type = "Corrective";

            default -> {
                // I allow the service to validate a name.
            }
        }

        ownerId = readPositiveInt(
                "Enter registered employee owner ID: ");

        if (ownerId <= 0) {
            return;
        }

        frequency = readText(
                "Control frequency (e.g. Every login): ");

        result = controlService.registerControl(
                riskId,
                controlName,
                objective,
                type,
                ownerId,
                frequency
        );

        // ========== OUTPUT ==========

        System.out.println(result);
    }

    // ========================================================
    // 6. VIEWING REGISTERED CONTROLS
    // ========================================================

    private void viewControlRegister() {

        System.out.println();
        System.out.println("----- CONTROL REGISTER -----");

        try {

            List<Control> controls =
                    controlService.getAllControls();

            if (controls.isEmpty()) {

                System.out.println(
                        "No controls have been registered.");

                return;
            }

            displayControls(controls);

        } catch (SQLException | IOException e) {

            System.out.println(
                    "Unable to load control register: "
                    + e.getMessage());
        }
    }

    // ========================================================
    // 7. VIEWING CONTROLS LINKED TO A RISK
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

        for (Control control : controls) {

            System.out.println();
            System.out.println(
                    "----------------------------------------");

            System.out.println(
                    "Control ID: " + control.getControlId());

            System.out.println(
                    "Risk ID: " + control.getRiskId());

            System.out.println(
                    "Name: " + control.getControlName());

            System.out.println(
                    "Objective: " + control.getControlObjective());

            System.out.println(
                    "Type: " + control.getControlType());

            System.out.println(
                    "Owner Employee ID: " + control.getOwnerId());

            System.out.println(
                    "Frequency: " + control.getFrequency());

            System.out.println(
                    "Status: " + control.getStatus());

            System.out.println(
                    "Created: " + control.getCreatedAt());
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
                "Assessment period: ");

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
                // My service validates typed conclusions.
            }
        }

        testerId = readPositiveInt(
                "Enter registered tester employee ID: ");

        if (testerId <= 0) {
            return;
        }

        result = testService.registerControlTest(
                controlId,
                procedure,
                period,
                observations,
                conclusion,
                testerId
        );

        // ========== OUTPUT ==========

        System.out.println(result);

        if (result.startsWith(
                "Control test recorded successfully")) {

            System.out.println(
                    "This conclusion is not yet approved.");

            System.out.println(
                    "Use option 5 to find the test ID, "
                    + "option 6 to register evidence, "
                    + "and option 8 for independent review.");
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
                        "Observations: " + test.getTestResults());

                System.out.println(
                        "Recorded Conclusion: "
                        + test.getConclusion());

                System.out.println(
                        "Tester Employee ID: " + test.getTestedBy());

                System.out.println(
                        "Recorded At: " + test.getTestedAt());

                // I clearly separate a proposed conclusion
                // from an independently reviewed assessment.
                AssessmentReview review =
                        reviewService.getReview(test.getTestId());

                if (review == null) {

                    System.out.println(
                            "Review Status: Pending Review");

                } else {

                    System.out.println(
                            "Review Status: " + review.getDecision());

                    System.out.println(
                            "Reviewer ID: " + review.getReviewerId());
                }
            }

        } catch (SQLException | IOException e) {

            System.out.println(
                    "Unable to load testing history: "
                    + e.getMessage());
        }
    }

    // ========================================================
    // 11. REGISTERING ASSESSMENT EVIDENCE
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

        source = readText("Evidence source: ");

        description = readText(
                "Describe what the evidence shows: ");

        reference = readText(
                "Evidence reference or document ID: ");

        // ========== PROCESSING ==========

        result = evidenceService.registerEvidence(
                testId,
                evidenceName,
                source,
                description,
                reference
        );

        // ========== OUTPUT ==========

        System.out.println(result);
    }

    // ========================================================
    // 12. VIEWING EVIDENCE
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
                        "Evidence ID: " + evidence.getEvidenceId());

                System.out.println(
                        "Control Test ID: " + evidence.getTestId());

                System.out.println(
                        "Evidence Name: " + evidence.getEvidenceName());

                System.out.println(
                        "Source: " + evidence.getEvidenceSource());

                System.out.println(
                        "Description: "
                        + evidence.getEvidenceDescription());

                System.out.println(
                        "Reference: " + evidence.getEvidenceReference());

                System.out.println(
                        "Recorded At: " + evidence.getRecordedAt());
            }

            System.out.println(
                    "These are evidence references, "
                    + "not verified files.");

        } catch (SQLException | IOException e) {

            System.out.println(
                    "Unable to retrieve evidence: "
                    + e.getMessage());
        }
    }

    // ========================================================
    // 13. REVIEWING AN ASSESSMENT - NEW
    // ========================================================

    private void reviewAssessment() {

        // ========== DECLARATIONS ==========

        int testId;
        int reviewerId;

        String choice;
        String decision;
        String evidenceAssessment;
        String reviewNotes;
        String result;

        // ========== INPUT ==========

        System.out.println();
        System.out.println(
                "----- INDEPENDENT ASSESSMENT REVIEW -----");

        testId = readPositiveInt("Enter control test ID: ");

        if (testId <= 0) {
            return;
        }

        reviewerId = readPositiveInt(
                "Enter reviewer employee ID: ");

        if (reviewerId <= 0) {
            return;
        }

        System.out.println();
        System.out.println("Review decisions:");
        System.out.println("1. Approved");
        System.out.println("2. Rejected");

        choice = readText("Choose review decision: ");

        // ========== PROCESSING ==========

        decision = switch (choice) {

            case "1" -> "Approved";
            case "2" -> "Rejected";
            default -> choice;
        };

        evidenceAssessment = readText(
                "Evaluate the evidence: ");

        reviewNotes = readText(
                "Explain your decision: ");

        // I let my service enforce the review rules.
        result = reviewService.reviewAssessment(
                testId,
                reviewerId,
                decision,
                evidenceAssessment,
                reviewNotes
        );

        // ========== OUTPUT ==========

        System.out.println();
        System.out.println(result);

        System.out.println(
                "Reminder: A saved review decision is not "
                + "independent proof that evidence is reliable.");
    }

    // ========================================================
    // 14. VIEWING AN ASSESSMENT REVIEW - NEW
    // ========================================================

    private void viewAssessmentReview() {

        // ========== DECLARATIONS ==========

        int testId;

        // ========== INPUT ==========

        System.out.println();
        System.out.println(
                "----- ASSESSMENT REVIEW RECORD -----");

        testId = readPositiveInt("Enter control test ID: ");

        if (testId <= 0) {
            return;
        }

        // ========== PROCESSING ==========

        try {

            AssessmentReview review =
                    reviewService.getReview(testId);

            // ========== OUTPUT ==========

            if (review == null) {

                System.out.println(
                        "No review has been recorded for test "
                        + testId + ".");

                return;
            }

            System.out.println();
            System.out.println(
                    "----------------------------------------");

            System.out.println(
                    "Review ID: " + review.getReviewId());

            System.out.println(
                    "Control Test ID: " + review.getTestId());

            System.out.println(
                    "Reviewer Employee ID: " + review.getReviewerId());

            System.out.println(
                    "Decision: " + review.getDecision());

            System.out.println(
                    "Evidence Assessment: "
                    + review.getEvidenceAssessment());

            System.out.println(
                    "Review Notes: " + review.getReviewNotes());

            System.out.println(
                    "Reviewed At: " + review.getReviewedAt());

            System.out.println(
                    "----------------------------------------");

        } catch (SQLException | IOException e) {

            System.out.println(
                    "Unable to retrieve review: "
                    + e.getMessage());
        }
    }

    // ========================================================
    // 15. READING NUMERIC INPUT
    // ========================================================

    private int readPositiveInt(String message) {

        // ========== DECLARATIONS ==========

        String enteredValue;
        int number;

        // ========== INPUT ==========

        System.out.print(message);
        enteredValue = input.nextLine().trim();

        // ========== VALIDATION ==========

        try {

            number = Integer.parseInt(enteredValue);

            if (number <= 0) {

                System.out.println(
                        "Please enter a number greater than zero.");

                return -1;
            }

            // ========== OUTPUT ==========

            return number;

        } catch (NumberFormatException e) {

            System.out.println(
                    "Invalid number. Please enter a whole number.");

            return -1;
        }
    }

    // ========================================================
    // 16. READING TEXT INPUT
    // ========================================================

    private String readText(String message) {

        // I read a full line and remove extra spaces.
        System.out.print(message);

        return input.nextLine().trim();
    }
}
