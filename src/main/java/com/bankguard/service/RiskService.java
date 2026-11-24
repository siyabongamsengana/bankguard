
package com.bankguard.service;

// ============================================================
// IMPORTS
// ============================================================

// I import my repositories so I can apply my risk
// management rules before changing database records.

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

    // I need both repositories to manage risks
    // and confirm that their owners exist.
    private final RiskRepository riskRepository;
    private final EmployeeRepository employeeRepository;


    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public RiskService() {

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

        // ========== VALIDATING REQUIRED INFORMATION ==========

        // I don't want incomplete risks in my register.
        if (title == null || title.isBlank()
                || description == null
                || description.isBlank()) {

            return "Risk name and description are required.";
        }


        // ========== VALIDATING MY RISK RATINGS ==========

        // I only allow ratings between 1 and 5.
        if (likelihood < 1 || likelihood > 5
                || impact < 1 || impact > 5) {

            return "Likelihood and impact must be 1 to 5.";
        }


        // ========== VALIDATING MY RISK OWNER ==========

        // I only accept positive employee IDs.
        if (ownerId <= 0) {

            return "Invalid risk owner ID.";
        }


        // ========== PROCESSING ==========

        try {

            // I check that the selected employee exists.
            if (!employeeRepository.employeeExists(ownerId)) {

                return "Risk owner not found. "
                        + "Choose a registered employee.";
            }

            // I also check whether this title already exists.
            if (riskRepository.riskExists(title)) {

                return "A risk with this name already exists.";
            }

            // I save the risk with its selected owner.
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
    // 4. VIEWING MY REGISTERED RISKS
    // ========================================================

    public List<Risk> getAllRisks()
            throws SQLException, IOException {

        return riskRepository.findAllRisks();
    }


    // ========================================================
    // 5. UPDATING MY RISK STATUS
    // ========================================================

    public String changeRiskStatus(
            int riskId, String newStatus) {

        // ========== DECLARATIONS ==========

        String validStatus;
        int rowsUpdated;


        // ========== INITIALISATION ==========

        validStatus = null;
        rowsUpdated = 0;


        // ========== VALIDATION ==========

        if (riskId <= 0) {

            return "Invalid risk ID.";
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

            rowsUpdated = riskRepository.updateStatus(
                    riskId, validStatus);


            // ========== OUTPUT ==========

            if (rowsUpdated > 0) {

                return "Risk status updated to: " + validStatus;
            }

            return "Risk not found. Check the risk ID.";

        } catch (SQLException | IOException e) {

            return "Status update failed: " + e.getMessage();
        }
    }


    // ========================================================
    // 6. CHANGING MY RISK OWNER
    // ========================================================

    public String changeRiskOwner(int riskId, int employeeId) {

        // ========== DECLARATIONS ==========

        int rowsUpdated;


        // ========== INITIALISATION ==========

        rowsUpdated = 0;


        // ========== VALIDATION ==========

        // I need valid IDs for both the risk and employee.
        if (riskId <= 0) {

            return "Invalid risk ID.";
        }

        if (employeeId <= 0) {

            return "Invalid employee ID.";
        }


        // ========== PROCESSING ==========

        try {

            // I check whether the new owner exists.
            if (!employeeRepository.employeeExists(employeeId)) {

                return "Employee not found. "
                        + "Register the employee first.";
            }

            // I update the employee responsible for this risk.
            rowsUpdated = riskRepository.updateRiskOwner(
                    riskId, employeeId);


            // ========== OUTPUT ==========

            if (rowsUpdated > 0) {

                return "Risk owner updated successfully!";
            }

            return "Risk not found. Check the risk ID.";

        } catch (SQLException | IOException e) {

            return "Risk owner update failed: "
                    + e.getMessage();
        }
    }
}
