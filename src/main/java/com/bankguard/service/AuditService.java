
package com.bankguard.service;

// ============================================================
// IMPORTS
// ============================================================

// I import my repository and model classes.
// I use this service to validate audit information.

import com.bankguard.model.AuditLog;
import com.bankguard.repository.AuditRepository;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AuditService {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private final AuditRepository auditRepository;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public AuditService() {

        // I create my repository to access audit records.
        auditRepository = new AuditRepository();
    }

    // ========================================================
    // 3. RECORDING A RISK CHANGE
    // ========================================================

    public void recordChange(
            Connection connection,
            int riskId,
            String action,
            String oldValue,
            String newValue,
            int changedBy)
            throws SQLException, IOException {

        // ========== PROCESSING ==========

        // I check that the risk ID is valid.
        if (riskId <= 0) {
            throw new IllegalArgumentException(
                    "Risk ID must be greater than zero.");
        }

        // I check that an employee ID was supplied.
        // SQLite will verify that the employee exists.
        if (changedBy <= 0) {
            throw new IllegalArgumentException(
                    "Employee ID must be greater than zero.");
        }

        // I only allow the audit actions supported
        // by this version of BankGuard.
        if (!"OWNER_CHANGED".equals(action)
                && !"STATUS_CHANGED".equals(action)) {

            throw new IllegalArgumentException(
                    "Unsupported audit action.");
        }

        // I make sure both values are available.
        if (oldValue == null || newValue == null) {
            throw new IllegalArgumentException(
                    "Audit values cannot be null.");
        }

        // I do not record a change if the values
        // are exactly the same.
        if (oldValue.equals(newValue)) {
            throw new IllegalArgumentException(
                    "Old and new values must be different.");
        }

        // I send valid information to my repository.
        auditRepository.saveAuditLog(
                connection,
                riskId,
                action,
                oldValue,
                newValue,
                changedBy
        );
    }

    // ========================================================
    // 4. RETRIEVING RISK HISTORY
    // ========================================================

    public List<AuditLog> getRiskHistory(int riskId)
            throws SQLException, IOException {

        // ========== PROCESSING ==========

        // I reject invalid risk IDs.
        if (riskId <= 0) {
            throw new IllegalArgumentException(
                    "Risk ID must be greater than zero.");
        }

        // ========== OUTPUT ==========

        // I retrieve the records for this risk.
        return auditRepository.findByRiskId(riskId);
    }
}