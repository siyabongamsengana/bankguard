
package com.bankguard.service;

// ============================================================
// BANKGUARD v1.1 - CONTROL SERVICE
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

// I use my repositories to verify risks and employees
// before registering controls in my bank's database.

import com.bankguard.model.Control;
import com.bankguard.model.Risk;
import com.bankguard.repository.ControlRepository;
import com.bankguard.repository.RiskRepository;
import com.bankguard.repository.EmployeeRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ControlService {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    // I use this repository to save and retrieve controls.
    private final ControlRepository controlRepository;

    // I use this repository to check registered risks.
    private final RiskRepository riskRepository;

    // I use this repository to verify control owners.
    private final EmployeeRepository employeeRepository;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public ControlService() {

        // I prepare all the repositories I need.
        controlRepository = new ControlRepository();
        riskRepository = new RiskRepository();
        employeeRepository = new EmployeeRepository();
    }

    // ========================================================
    // 3. REGISTERING A NEW CONTROL
    // ========================================================

    public String registerControl(
            int riskId,
            String controlName,
            String controlObjective,
            String controlType,
            int ownerId,
            String frequency) {

        // ========== DECLARATIONS ==========

        String validType;
        int rowsInserted;
        boolean riskFound;

        // ========== INITIALISATION ==========

        validType = null;
        rowsInserted = 0;
        riskFound = false;

        // ========== VALIDATION ==========

        // I require an existing risk to link my control to.
        if (riskId <= 0) {

            return "Invalid risk ID.";
        }

        // I require an employee to take ownership.
        if (ownerId <= 0) {

            return "Invalid control owner ID.";
        }

        // I require a meaningful control name.
        if (controlName == null || controlName.isBlank()) {

            return "Control name is required.";
        }

        // I require the objective the control addresses.
        if (controlObjective == null
                || controlObjective.isBlank()) {

            return "Control objective is required.";
        }

        // I record how often the control should operate.
        if (frequency == null || frequency.isBlank()) {

            return "Control frequency is required.";
        }

        // I limit field lengths to manageable values.
        if (controlName.trim().length() > 150) {

            return "Control name cannot exceed 150 characters.";
        }

        if (controlObjective.trim().length() > 2000) {

            return "Control objective cannot exceed "
                    + "2000 characters.";
        }

        if (frequency.trim().length() > 150) {

            return "Control frequency cannot exceed "
                    + "150 characters.";
        }

        // I check that the type matches my control register.
        if (controlType != null) {

            switch (controlType.trim().toLowerCase()) {

                case "preventive" ->
                    validType = "Preventive";

                case "detective" ->
                    validType = "Detective";

                case "corrective" ->
                    validType = "Corrective";

                default ->
                    validType = null;
            }
        }

        if (validType == null) {

            return "Invalid control type. Choose Preventive, "
                    + "Detective or Corrective.";
        }

        // ========== PROCESSING ==========

        try {

            // I confirm the selected risk exists.
            // My existing risk repository can list risks.
            for (Risk risk : riskRepository.findAllRisks()) {

                if (risk.getRiskId() == riskId) {

                    riskFound = true;
                    break;
                }
            }

            if (!riskFound) {

                return "Risk not found. "
                        + "Register the risk first.";
            }

            // I confirm the control owner exists.
            if (!employeeRepository.employeeExists(ownerId)) {

                return "Control owner not found. "
                        + "Choose a registered employee.";
            }

            // I save the control with validated information.
            rowsInserted = controlRepository.registerControl(
                    riskId,
                    controlName.trim(),
                    controlObjective.trim(),
                    validType,
                    ownerId,
                    frequency.trim()
            );

            // ========== OUTPUT ==========

            if (rowsInserted > 0) {

                return "Control registered successfully!";
            }

            return "Control was not registered.";

        } catch (SQLException | IOException e) {

            return "Control registration failed: "
                    + e.getMessage();
        }
    }

    // ========================================================
    // 4. VIEWING ALL REGISTERED CONTROLS
    // ========================================================

    public List<Control> getAllControls()
            throws SQLException, IOException {

        // I retrieve the full control register.
        return controlRepository.findAllControls();
    }

    // ========================================================
    // 5. VIEWING CONTROLS LINKED TO ONE RISK
    // ========================================================

    public List<Control> getControlsByRisk(int riskId)
            throws SQLException, IOException {

        // ========== VALIDATION ==========

        if (riskId <= 0) {

            throw new IllegalArgumentException(
                    "Risk ID must be greater than zero.");
        }

        // ========== OUTPUT ==========

        // I retrieve controls linked to the selected risk.
        return controlRepository.findControlsByRisk(riskId);
    }

    // ========================================================
    // 6. CHECKING WHETHER A CONTROL EXISTS
    // ========================================================

    public boolean controlExists(int controlId)
            throws SQLException, IOException {

        // ========== VALIDATION ==========

        if (controlId <= 0) {

            return false;
        }

        // ========== PROCESSING ==========

        // I check my existing control register.
        // This works for my small educational database.
        for (Control control :
                controlRepository.findAllControls()) {

            if (control.getControlId() == controlId) {

                return true;
            }
        }

        // ========== OUTPUT ==========

        return false;
    }
}
