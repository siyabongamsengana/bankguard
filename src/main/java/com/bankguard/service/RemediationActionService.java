
package com.bankguard.service;

// ============================================================
// BANKGUARD v1.2 - REMEDIATION ACTION SERVICE
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

// I import my remediation model and repositories.
// I use them to manage and validate corrective actions.

import com.bankguard.model.RemediationAction;
import com.bankguard.repository.RemediationActionRepository;
import com.bankguard.repository.EmployeeRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class RemediationActionService {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    // I use this repository to save, retrieve
    // and complete remediation actions.
    private final RemediationActionRepository actionRepository;

    // I use this repository to verify employee IDs.
    private final EmployeeRepository employeeRepository;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public RemediationActionService() {

        // I prepare my repositories.
        actionRepository = new RemediationActionRepository();
        employeeRepository = new EmployeeRepository();
    }

    // ========================================================
    // 3. REGISTERING A CORRECTIVE ACTION
    // ========================================================

    public String registerAction(
            int findingId,
            String description,
            int assignedTo,
            String dueDate) {

        // ========== DECLARATIONS ==========

        LocalDate deadline;
        int rowsInserted;

        // ========== INITIALISATION ==========

        rowsInserted = 0;

        // ========== VALIDATION ==========

        // I require a valid finding ID.
        if (findingId <= 0) {

            return "Invalid finding ID.";
        }

        // I require a registered employee ID.
        if (assignedTo <= 0) {

            return "Invalid assigned employee ID.";
        }

        // I require a description of the corrective work.
        if (description == null || description.isBlank()) {

            return "Action description is required.";
        }

        // I limit the length of the action description.
        if (description.trim().length() > 3000) {

            return "Action description cannot exceed "
                    + "3000 characters.";
        }

        // I require a remediation deadline.
        if (dueDate == null || dueDate.isBlank()) {

            return "Action due date is required.";
        }

        // I check that the date is valid.
        try {

            deadline = LocalDate.parse(dueDate.trim());

        } catch (DateTimeParseException e) {

            return "Invalid due date. Use YYYY-MM-DD.";
        }

        // I prevent new actions from having past deadlines.
        if (deadline.isBefore(LocalDate.now())) {

            return "Action due date cannot be in the past.";
        }

        // ========== PROCESSING ==========

        try {

            // I confirm that the finding exists.
            if (!actionRepository.findingExists(findingId)) {

                return "Finding not found. "
                        + "Register the finding first.";
            }

            // I confirm that the employee exists.
            if (!employeeRepository.employeeExists(assignedTo)) {

                return "Assigned employee not found. "
                        + "Choose a registered employee.";
            }

            // I register the corrective action.
            rowsInserted = actionRepository.registerAction(
                    findingId,
                    description.trim(),
                    assignedTo,
                    deadline.toString()
            );

            // ========== OUTPUT ==========

            if (rowsInserted > 0) {

                return "Remediation action registered successfully!";
            }

            return "Remediation action was not registered.";

        } catch (SQLException | IOException e) {

            return "Remediation registration failed: "
                    + e.getMessage();
        }
    }

    // ========================================================
    // 4. VIEWING ACTIONS LINKED TO A FINDING
    // ========================================================

    public List<RemediationAction> getActionsByFinding(
            int findingId)
            throws SQLException, IOException {

        // ========== VALIDATION ==========

        // I require a valid finding ID.
        if (findingId <= 0) {

            throw new IllegalArgumentException(
                    "Finding ID must be greater than zero.");
        }

        // ========== OUTPUT ==========

        // I retrieve corrective actions linked
        // to the selected finding.
        return actionRepository.findActionsByFinding(findingId);
    }

    // ========================================================
    // 5. COMPLETING A REMEDIATION ACTION
    // ========================================================

    public String completeAction(
            int actionId,
            int actingEmployeeId,
            String evidenceReference) {

        // ========== DECLARATIONS ==========

        int rowsUpdated;

        // ========== INITIALISATION ==========

        rowsUpdated = 0;

        // ========== VALIDATION ==========

        // I require a valid remediation action ID.
        if (actionId <= 0) {

            return "Invalid remediation action ID.";
        }

        // I require a valid employee ID.
        if (actingEmployeeId <= 0) {

            return "Invalid acting employee ID.";
        }

        // I require evidence before marking
        // corrective work as completed.
        if (evidenceReference == null
                || evidenceReference.isBlank()) {

            return "Completion evidence is required.";
        }

        // I limit the evidence reference length.
        if (evidenceReference.trim().length() > 2000) {

            return "Completion evidence cannot exceed "
                    + "2000 characters.";
        }

        // ========== PROCESSING ==========

        try {

            // I confirm that the reported employee
            // exists in the employee register.
            if (!employeeRepository.employeeExists(
                    actingEmployeeId)) {

                return "Acting employee not found.";
            }

            // I let my repository check whether:
            // 1. The action exists.
            // 2. The employee is assigned to the action.
            // 3. The action is not already completed.
            // 4. The linked finding is not closed.
            //
            // I save the status and evidence together.
            rowsUpdated = actionRepository.completeAction(
                    actionId,
                    actingEmployeeId,
                    evidenceReference.trim()
            );

            // ========== OUTPUT ==========

            if (rowsUpdated == 1) {

                return "Remediation action marked as completed! "
                        + "Independent verification is still "
                        + "required before finding closure.";
            }

            // I reject completion if the database
            // did not update the selected action.
            return "Completion rejected. The action may not "
                    + "exist, may belong to another employee, "
                    + "may already be completed, or its "
                    + "finding may be closed.";

        } catch (SQLException | IOException e) {

            return "Remediation completion failed: "
                    + e.getMessage();
        }
    }
}
