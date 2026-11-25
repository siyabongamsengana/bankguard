
-- ============================================================
-- VIEWING A RISK'S AUDIT HISTORY
-- ============================================================

-- I retrieve the history for one particular risk.
-- I show the oldest changes first to follow the timeline.

SELECT
    audit_id,
    risk_id,
    action,
    old_value,
    new_value,
    changed_by,
    changed_at
FROM audit_logs
WHERE risk_id = ?
ORDER BY changed_at ASC, audit_id ASC;