
-- ============================================================
-- BANKGUARD v0.9 - AUDIT TRAIL TABLE
-- ============================================================

-- I create a table to keep a history of changes made to risks.
-- I use IF NOT EXISTS so I do not recreate an existing table.
-- This also helps me protect my existing database records.

CREATE TABLE IF NOT EXISTS audit_logs (

    -- I give every audit record its own unique ID.
    audit_id INTEGER PRIMARY KEY AUTOINCREMENT,

    -- I link the audit record to the risk that was changed.
    risk_id INTEGER NOT NULL,

    -- I record what type of change took place.
    -- Examples: OWNER_CHANGED or STATUS_CHANGED.
    action TEXT NOT NULL
        CHECK (action IN ('OWNER_CHANGED', 'STATUS_CHANGED')),

    -- I save the value before the change.
    old_value TEXT NOT NULL,

    -- I save the value after the change.
    new_value TEXT NOT NULL,

    -- I record the employee ID supplied by the person
    -- performing the change.
    -- This does not verify their identity.
    changed_by INTEGER NOT NULL,

    -- I automatically record the date and time in UTC.
    changed_at TEXT NOT NULL
        DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),

    -- I make sure audit records refer to valid risks.
    FOREIGN KEY (risk_id)
        REFERENCES risks(risk_id),

    -- I link the acting employee to the users table.
    FOREIGN KEY (changed_by)
        REFERENCES users(user_id)
);

-- I create an index to make it easier to find
-- the audit history for a specific risk.

CREATE INDEX IF NOT EXISTS idx_audit_logs_risk_id
ON audit_logs(risk_id);