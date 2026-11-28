
package com.bankguard.service;

// ============================================================
// BANKGUARD v1.2 - FINDING CLOSURE SERVICE
// ============================================================

// ============================================================
// IMPORTS
// ============================================================

import com.bankguard.repository.FindingClosureRepository;

import java.io.IOException;
import java.sql.SQLException;

public class FindingClosureService {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private final FindingClosureRepository closureRepository;

    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public FindingClosureService() {

        // I prepare the repository responsible
        // for enforcing closure conditions.
        closureRepository = new FindingClosureRepository();
    }

    // ========================================================
    // 3. CLOSING A REMEDIATED FINDING
    // ========================================================

    public String closeFinding(
            int findingId,
            int closedBy,
            String closureNotes) {

        // ========== DECLARATIONS ==========

        int rowsUpdated;

        // ========== INITIALISATION ==========

        rowsUpdated = 0;

        // ========== VALIDATION ==========

        // I require a valid finding ID.
        if (findingId <= 0) {

            return "Invalid finding ID.";
        }

        // I require a registered employee to report
        // the formal closure decision.
        if (closedBy <= 0) {

            return "Invalid closure employee ID.";
        }

        // I require a written closure justification.
        if (closureNotes == null
                || closureNotes.isBlank()) {

            return "Closure notes are required.";
        }

        // I limit the length of the justification.
        if (closureNotes.trim().length() > 3000) {

            return "Closure notes cannot exceed "
                    + "3000 characters.";
        }

        // ========== PROCESSING ==========

        try {

            // I let my repository confirm:
            // 1. The finding exists and is not closed.
            // 2. The closure employee is registered.
            // 3. All corrective actions are completed.
            // 4. Every action has an evidence reference.
            // 5. The latest independent verification
            //    decision is Verified.
            //
            // I save the closure record and status
            // change in the same database transaction.

            rowsUpdated = closureRepository.closeFinding(
                    findingId,
                    closedBy,
                    closureNotes.trim()
            );

            // ========== OUTPUT ==========

            if (rowsUpdated == 1) {

                return "Finding closed successfully! "
                        + "The closure decision and supporting "
                        + "verification have been recorded.";
            }

            return "Finding was not closed.";

        } catch (IllegalArgumentException e) {

            return "Finding closure rejected: "
                    + e.getMessage();

        } catch (SQLException | IOException e) {

            return "Finding closure failed: "
                    + e.getMessage();
        }
    }
}