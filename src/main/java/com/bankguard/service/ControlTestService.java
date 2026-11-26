
package com.bankguard.service;

// ============================================================
// BANKGUARD v1.1 - CONTROL TEST SERVICE
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

// I use my repositories to check controls and employees.
// I save and retrieve assessments of security controls.

import com.bankguard.model.ControlTest;
import com.bankguard.repository.ControlTestRepository;
import com.bankguard.repository.EmployeeRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ControlTestService {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private final ControlTestRepository testRepository;

    // I use this service to check registered controls.
    private final ControlService controlService;

    // I verify the employee ID supplied by the tester.
    private final EmployeeRepository employeeRepository;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public ControlTestService() {

        testRepository = new ControlTestRepository();
        controlService = new ControlService();
        employeeRepository = new EmployeeRepository();
    }

    // ========================================================
    // 3. REGISTERING A CONTROL TEST
    // ========================================================

    public String registerControlTest(
            int controlId,
            String testProcedure,
            String assessmentPeriod,
            String testResults,
            String conclusion,
            int testedBy) {

        // ========== DECLARATIONS ==========

        String validConclusion;
        int rowsInserted;

        // ========== INITIALISATION ==========

        validConclusion = null;
        rowsInserted = 0;

        // ========== VALIDATION ==========

        // I need a valid control ID.
        if (controlId <= 0) {

            return "Invalid control ID.";
        }

        // I require a documented testing procedure.
        if (testProcedure == null
                || testProcedure.isBlank()) {

            return "Test procedure is required.";
        }

        // I need to know the period covered by the test.
        if (assessmentPeriod == null
                || assessmentPeriod.isBlank()) {

            return "Assessment period is required.";
        }

        // I record observations even when no exceptions
        // were found or the results are inconclusive.
        if (testResults == null || testResults.isBlank()) {

            return "Test observations or results are required.";
        }

        // I require an employee ID for the person
        // reporting that they performed the assessment.
        if (testedBy <= 0) {

            return "Invalid tester employee ID.";
        }

        // I limit the length of my assessment fields.
        if (testProcedure.trim().length() > 3000) {

            return "Test procedure cannot exceed "
                    + "3000 characters.";
        }

        if (assessmentPeriod.trim().length() > 150) {

            return "Assessment period cannot exceed "
                    + "150 characters.";
        }

        if (testResults.trim().length() > 5000) {

            return "Test results cannot exceed "
                    + "5000 characters.";
        }

        // I only accept conclusions defined in my database.
        if (conclusion != null) {

            switch (conclusion.trim().toLowerCase()) {

                case "not tested" ->
                    validConclusion = "Not Tested";

                case "effective" ->
                    validConclusion = "Effective";

                case "ineffective" ->
                    validConclusion = "Ineffective";

                case "inconclusive" ->
                    validConclusion = "Inconclusive";

                default ->
                    validConclusion = null;
            }
        }

        if (validConclusion == null) {

            return "Invalid conclusion. Choose Not Tested, "
                    + "Effective, Ineffective or Inconclusive.";
        }

        // ========== PROCESSING ==========

        try {

            // I check that the control is registered.
            if (!controlService.controlExists(controlId)) {

                return "Control not found. "
                        + "Register the control first.";
            }

            // I confirm the reported tester is registered.
            if (!employeeRepository.employeeExists(testedBy)) {

                return "Tester not found. "
                        + "Choose a registered employee.";
            }

            // I save my assessment in the database.
            rowsInserted = testRepository.registerControlTest(
                    controlId,
                    testProcedure.trim(),
                    assessmentPeriod.trim(),
                    testResults.trim(),
                    validConclusion,
                    testedBy
            );

            // ========== OUTPUT ==========

            if (rowsInserted > 0) {

                return "Control test recorded successfully! "
                        + "Evidence supporting the conclusion "
                        + "must still be reviewed.";
            }

            return "Control test was not recorded.";

        } catch (SQLException | IOException e) {

            return "Control test registration failed: "
                    + e.getMessage();
        }
    }

    // ========================================================
    // 4. VIEWING PREVIOUS TESTS FOR ONE CONTROL
    // ========================================================

    public List<ControlTest> getTestsByControl(int controlId)
            throws SQLException, IOException {

        // ========== VALIDATION ==========

        if (controlId <= 0) {

            throw new IllegalArgumentException(
                    "Control ID must be greater than zero.");
        }

        // ========== OUTPUT ==========

        return testRepository.findTestsByControl(controlId);
    }

    // ========================================================
    // 5. CHECKING WHETHER A CONTROL TEST EXISTS
    // ========================================================

    public boolean testExists(int testId)
            throws SQLException, IOException {

        // ========== VALIDATION ==========

        if (testId <= 0) {

            return false;
        }

        // ========== PROCESSING ==========

        // I find the registered controls first.
        // I then check their recorded assessments.
        //
        // This is suitable for my small practice dataset.
        // Later I can replace it with a direct SQL lookup.

        for (com.bankguard.model.Control control :
                controlService.getAllControls()) {

            for (ControlTest test :
                    testRepository.findTestsByControl(
                            control.getControlId())) {

                if (test.getTestId() == testId) {

                    return true;
                }
            }
        }

        // ========== OUTPUT ==========

        return false;
    }
}
