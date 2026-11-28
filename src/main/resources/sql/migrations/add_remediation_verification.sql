
-- ============================================================
-- BANKGUARD v1.2 - REMEDIATION VERIFICATION MIGRATION
-- ============================================================

-- I keep verification decisions separate from the
-- employee's reported completion of corrective work.
--
-- One finding can have multiple verification attempts.
-- This preserves rejected attempts and later retesting.

CREATE TABLE IF NOT EXISTS grc_remediation_verifications (

    -- I assign a unique ID to every verification attempt.
    verification_id INTEGER PRIMARY KEY AUTOINCREMENT,

    -- I link the verification to an existing finding.
    finding_id INTEGER NOT NULL,

    -- I record the registered employee who verifies
    -- whether the corrective work was effective.
    reviewer_id INTEGER NOT NULL,

    -- I record the independent verification decision.
    decision TEXT NOT NULL
        CHECK (decision IN (
            'Verified',
            'Rejected'
        )),

    -- I document the evidence considered by the reviewer.
    evidence_assessment TEXT NOT NULL
        CHECK (length(trim(evidence_assessment)) > 0),

    -- I preserve the reviewer's explanation.
    verification_notes TEXT NOT NULL
        CHECK (length(trim(verification_notes)) > 0),

    -- I record when the verification took place.
    verified_at TEXT NOT NULL
        DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),

    -- I protect the relationship to the original finding.
    FOREIGN KEY (finding_id)
        REFERENCES grc_findings(finding_id),

    -- I require a reviewer from the employee register.
    FOREIGN KEY (reviewer_id)
        REFERENCES users(user_id)
);

-- ============================================================
-- INDEXES
-- ============================================================

-- I retrieve verification history for a finding.
CREATE INDEX IF NOT EXISTS idx_verifications_finding
ON grc_remediation_verifications(
    finding_id,
    verification_id
);

-- I can retrieve verifications by the reviewer.
CREATE INDEX IF NOT EXISTS idx_verifications_reviewer
ON grc_remediation_verifications(reviewer_id);
