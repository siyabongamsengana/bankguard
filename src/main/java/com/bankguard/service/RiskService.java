
package com.bankguard.service;

// ============================================================
// IMPORTS
// ============================================================

// I import my repositories and risk model.
// I use these classes to manage risks and validate
// information before changing my SQLite database.

import com.bankguard.model.Risk;
import com.bankguard.repository.RiskRepository;
import com.bankguard.repository.EmployeeRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class RiskService {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    // I use my risk repository to manage risk records.
    private final RiskRepository riskRepository;

    // I use my employee repository to verify employees.
    private final EmployeeRepository employeeRepository;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public RiskService() {

        // I initialise both repositories.
        riskRepository = new RiskRepository();
        employeeRepository = new EmployeeRepository();
    }

    // ========================================================
    // 3. REGISTERING A NEW RISK
    // ========================================================

    public String registerRisk(
            String title,
            String description,
            int likelihood,
            int impact,
            int ownerId) {

        // ========== VALIDATION ==========

        // I do not allow risks without a title
        // or description.
        if (title == null || title.isBlank()
                || description == null
                || description.isBlank()) {

            return "Risk name and description are required.";
        }

        // I only allow risk ratings between 1 and 5.
        if (likelihood < 1 || likelihood > 5
                || impact < 1 || impact > 5) {

            return "Likelihood and impact must be 1 to 5.";
        }

        // I require a positive risk owner ID.
        if (ownerId <= 0) {

            return "Invalid risk owner ID.";
        }

        // ========== PROCESSING ==========

        try {

            // I check whether the selected owner exists.
            if (!employeeRepository.employeeExists(ownerId)) {

                return "Risk owner not found. "
                        + "Choose a registered employee.";
            }

            // I reject duplicate risk titles.
            if (riskRepository.riskExists(title.trim())) {

                return "A risk with this name already exists.";
            }

            // I save the risk with its selected employee.
            riskRepository.saveRisk(
                    title.trim(),
                    description.trim(),
                    likelihood,
                    impact,
                    ownerId
            );

            // ========== OUTPUT ==========

            return "Risk registered successfully!";

        } catch (SQLException | IOException e) {

            return "Risk registration failed: "
                    + e.getMessage();
        }
    }

    // ========================================================
    // 4. VIEWING REGISTERED RISKS
    // ========================================================

    public List<Risk> getAllRisks()
            throws SQLException, IOException {

        // I retrieve my risk register from SQLite.
        return riskRepository.findAllRisks();
    }

    // ========================================================
    // 5. UPDATING RISK STATUS WITH ACCOUNTABILITY
    // ========================================================

    public String changeRiskStatus(
            int riskId,
            String newStatus,
            int changedBy) {

        // ========== DECLARATIONS ==========

        String validStatus;
        int rowsUpdated;

        // ========== INITIALISATION ==========

        validStatus = null;
        rowsUpdated = 0;

        // ========== VALIDATION ==========

        // I check whether a valid risk ID was supplied.
        if (riskId <= 0) {

            return "Invalid risk ID.";
        }

        // I require an employee ID for accountability.
        if (changedBy <= 0) {

            return "Invalid acting employee ID.";
        }

        // I check whether BankGuard supports the status.
        if (newStatus != null) {

            switch (newStatus.trim().toLowerCase()) {

                case "open" ->
                    validStatus = "Open";

                case "in progress" ->
                    validStatus = "In Progress";

                case "mitigated" ->
                    validStatus = "Mitigated";

                case "closed" ->
                    validStatus = "Closed";

                default ->
                    validStatus = null;
            }
        }

        if (validStatus == null) {

            return "Invalid risk status.";
        }

        // ========== PROCESSING ==========

        try {

            // I check that the person reporting the change
            // has an employee record in my database.
            if (!employeeRepository.employeeExists(changedBy)) {

                return "Acting employee not found. "
                        + "Choose a registered employee.";
            }

            // I update the risk status.
            // My repository also records the audit evidence
            // within the same database transaction.
            rowsUpdated = riskRepository.updateStatus(
                    riskId,
                    validStatus,
                    changedBy
            );

            // ========== OUTPUT ==========

            if (rowsUpdated > 0) {

                return "Risk status updated to: "
                        + validStatus
                        + ". Audit record saved.";
            }

            // My repository returns zero when the risk
            // does not exist or its status is unchanged.
            return "No change made. Risk not found "
                    + "or status is already " + validStatus + ".";

        } catch (SQLException | IOException e) {

            return "Status update failed: "
                    + e.getMessage();

        } catch (IllegalArgumentException e) {

            return "Status update rejected: "
                    + e.getMessage();
        }
    }

    // ========================================================
    // 6. CHANGING RISK OWNERSHIP WITH ACCOUNTABILITY
    // ========================================================

    public String changeRiskOwner(
            int riskId,
            int employeeId,
            int changedBy) {

        // ========== DECLARATIONS ==========

        int rowsUpdated;

        // ========== INITIALISATION ==========

        rowsUpdated = 0;

        // ========== VALIDATION ==========

        // I check that the risk ID is valid.
        if (riskId <= 0) {

            return "Invalid risk ID.";
        }

        // I check that the new owner ID is valid.
        if (employeeId <= 0) {

            return "Invalid new owner ID.";
        }

        // I require an employee ID for the person
        // who reports making the change.
        if (changedBy <= 0) {

            return "Invalid acting employee ID.";
        }

        // ========== PROCESSING ==========

        try {

            // I confirm that the new risk owner exists.
            if (!employeeRepository.employeeExists(employeeId)) {

                return "New risk owner not found. "
                        + "Register the employee first.";
            }

            // I confirm that the acting employee exists.
            if (!employeeRepository.employeeExists(changedBy)) {

                return "Acting employee not found. "
                        + "Choose a registered employee.";
            }

            // I update the risk owner.
            // My repository also records the old owner,
            // the new owner and the acting employee.
            rowsUpdated = riskRepository.updateRiskOwner(
                    riskId,
                    employeeId,
                    changedBy
            );

            // ========== OUTPUT ==========

            if (rowsUpdated > 0) {

                return "Risk owner updated successfully! "
                        + "Audit record saved.";
            }

            // A zero result means the risk was not found
            // or the owner was already assigned.
            return "No change made. Risk not found "
                    + "or employee is already the owner.";

        } catch (SQLException | IOException e) {

            return "Risk owner update failed: "
                    + e.getMessage();

        } catch (IllegalArgumentException e) {

            return "Risk owner update rejected: "
                    + e.getMessage();
        }
    }
}
