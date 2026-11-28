
-- ============================================================
-- BANKGUARD v1.2 - REGISTER FINDING CLOSURE
-- ============================================================

-- I preserve the formal decision to close a finding.

INSERT INTO grc_finding_closures (
    finding_id,
    verification_id,
    closed_by,
    closure_notes
)
VALUES (?, ?, ?, ?);