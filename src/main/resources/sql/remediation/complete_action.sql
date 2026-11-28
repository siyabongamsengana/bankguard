
-- ============================================================
-- BANKGUARD v1.2 - COMPLETE REMEDIATION ACTION
-- ============================================================

-- I mark corrective work as completed only when:
-- 1. The action exists.
-- 2. The reported employee is assigned to the action.
-- 3. The action is not already completed.
-- 4. The finding has not been closed.
--
-- I save the evidence reference with the status change.

UPDATE grc_remediation_actions

SET
    status = 'Completed',
    completion_evidence = ?

WHERE action_id = ?
AND assigned_to = ?
AND status IN ('Open', 'In Progress')

AND EXISTS (
    SELECT 1
    FROM grc_findings
    WHERE grc_findings.finding_id =
          grc_remediation_actions.finding_id
    AND grc_findings.status <> 'Closed'
);
