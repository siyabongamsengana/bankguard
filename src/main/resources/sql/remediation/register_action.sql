
-- ============================================================
-- BANKGUARD v1.2 - REGISTER REMEDIATION ACTION
-- ============================================================

-- I assign corrective work to a registered employee.

INSERT INTO grc_remediation_actions (
    finding_id,
    action_description,
    assigned_to,
    due_date
)
VALUES (?, ?, ?, ?);
