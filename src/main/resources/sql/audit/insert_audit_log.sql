
-- ============================================================
-- SAVING AN AUDIT RECORD
-- ============================================================

-- I save evidence of a change made to an existing risk.
-- I record what changed, the old and new values,
-- and the employee ID supplied by the person making it.

-- SQLite automatically creates the audit ID and timestamp.

INSERT INTO audit_logs (
    risk_id,
    action,
    old_value,
    new_value,
    changed_by
)
VALUES (?, ?, ?, ?, ?);