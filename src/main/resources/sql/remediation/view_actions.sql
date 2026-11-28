
-- ============================================================
-- BANKGUARD v1.2 - VIEW REMEDIATION ACTIONS
-- ============================================================

-- I retrieve actions linked to one finding.

SELECT
    action_id,
    finding_id,
    action_description,
    assigned_to,
    due_date,
    status,
    completion_evidence,
    created_at
FROM grc_remediation_actions
WHERE finding_id = ?
ORDER BY action_id;
