
package com.bankguard.service;

// ============================================================
// BANKGUARD v1.2 - FINDING SERVICE
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

import com.bankguard.model.Finding;
import com.bankguard.repository.FindingRepository;
import com.bankguard.repository.EmployeeRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class FindingService {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private final FindingRepository findingRepository;
    private final EmployeeRepository employeeRepository;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public FindingService() {

        findingRepository = new FindingRepository();
        employeeRepository = new EmployeeRepository();
    }

    // ========================================================
    // 3. REGISTERING A NEW FINDING
    // ========================================================

    public String registerFinding(
            int testId,
            String title,
            String description,
            String severity,
            int ownerId,
            String dueDate) {

        // ========== DECLARATIONS ==========

        String validSeverity;
        LocalDate deadline;
        int rowsInserted;

        // ========== INITIALISATION ==========

        validSeverity = null;
        rowsInserted = 0;

        // ========== VALIDATION ==========

        // I require an existing control assessment.
        if (testId <= 0) {

            return "Invalid control assessment ID.";
        }

        // I require an employee to own the finding.
        if (ownerId <= 0) {

            return "Invalid finding owner ID.";
        }

        // I require a meaningful finding title.
        if (title == null || title.isBlank()) {

            return "Finding title is required.";
        }

        // I require a clear description of the weakness.
        if (description == null || description.isBlank()) {

            return "Finding description is required.";
        }

        // I prevent excessively long entries.
        if (title.trim().length() > 150) {

            return "Finding title cannot exceed 150 characters.";
        }

        if (description.trim().length() > 3000) {

            return "Finding description cannot exceed "
                    + "3000 characters.";
        }

        // I only allow supported severity ratings.
        if (severity != null) {

            switch (severity.trim().toLowerCase()) {

                case "low" ->
                    validSeverity = "Low";

                case "medium" ->
                    validSeverity = "Medium";

                case "high" ->
                    validSeverity = "High";

                case "critical" ->
                    validSeverity = "Critical";

                default ->
                    validSeverity = null;
            }
        }

        if (validSeverity == null) {

            return "Invalid severity. Choose Low, Medium, "
                    + "High or Critical.";
        }

        // I require an actual calendar date.
        if (dueDate == null || dueDate.isBlank()) {

            return "Remediation due date is required.";
        }

        try {

            // I use ISO format: YYYY-MM-DD.
            deadline = LocalDate.parse(dueDate.trim());

        } catch (DateTimeParseException e) {

            return "Invalid due date. Use YYYY-MM-DD.";
        }

        // I do not allow new findings to be given
        // deadlines that have already passed.
        if (deadline.isBefore(LocalDate.now())) {

            return "Remediation due date cannot be "
                    + "in the past.";
        }

        // ========== PROCESSING ==========

        try {

            // I confirm that the assessment exists.
            if (!findingRepository.assessmentExists(testId)) {

                return "Control assessment not found.";
            }

            // I confirm that the responsible employee exists.
            if (!employeeRepository.employeeExists(ownerId)) {

                return "Finding owner not found. "
                        + "Choose a registered employee.";
            }

            // I save the validated finding.
            rowsInserted = findingRepository.registerFinding(
                    testId,
                    title.trim(),
                    description.trim(),
                    validSeverity,
                    ownerId,
                    deadline.toString()
            );

            // ========== OUTPUT ==========

            if (rowsInserted > 0) {

                return "Finding registered successfully!";
            }

            return "Finding was not registered.";

        } catch (SQLException | IOException e) {

            return "Finding registration failed: "
                    + e.getMessage();
        }
    }

    // ========================================================
    // 4. VIEWING ALL FINDINGS
    // ========================================================

    public List<Finding> getAllFindings()
            throws SQLException, IOException {

        // I return the findings saved in my database.
        return findingRepository.findAllFindings();
    }
}

