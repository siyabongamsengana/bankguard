
-- ============================================================
-- BANKGUARD v1.2 - FINDING CLOSURE MIGRATION
-- ============================================================

-- I preserve a separate record of every finding closure.
-- A finding can only have one closure record in this version.

CREATE TABLE IF NOT EXISTS grc_finding_closures (

    -- I assign a unique ID to each closure decision.
    closure_id INTEGER PRIMARY KEY AUTOINCREMENT,

    -- I link the closure to its original finding.
    finding_id INTEGER NOT NULL UNIQUE,

    -- I link the closure to the successful
    -- remediation verification that supported it.
    verification_id INTEGER NOT NULL,

    -- I record the registered employee
    -- who made the closure decision.
    closed_by INTEGER NOT NULL,

    -- I require written reasons for closing the finding.
    closure_notes TEXT NOT NULL
        CHECK (length(trim(closure_notes)) > 0),

    -- I record when the closure decision was saved.
    closed_at TEXT NOT NULL
        DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),

    -- I protect the links between records.
    FOREIGN KEY (finding_id)
        REFERENCES grc_findings(finding_id),

    FOREIGN KEY (verification_id)
        REFERENCES grc_remediation_verifications(verification_id),

    FOREIGN KEY (closed_by)
        REFERENCES users(user_id)
);

-- ============================================================
-- INDEXES
-- ============================================================

-- I make it easier to retrieve closure decisions
-- by the employee who recorded them.
CREATE INDEX IF NOT EXISTS idx_closure_employee
ON grc_finding_closures(closed_by);
