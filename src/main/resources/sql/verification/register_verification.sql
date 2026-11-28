
-- ============================================================
-- BANKGUARD v1.2 - REGISTER REMEDIATION VERIFICATION
-- ============================================================

-- I preserve the reviewer's decision and written reasons.

INSERT INTO grc_remediation_verifications (
    finding_id,
    reviewer_id,
    decision,
    evidence_assessment,
    verification_notes
)
VALUES (?, ?, ?, ?, ?);