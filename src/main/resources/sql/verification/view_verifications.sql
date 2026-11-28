
-- ============================================================
-- BANKGUARD v1.2 - VIEW VERIFICATION HISTORY
-- ============================================================

-- I preserve all verification attempts for one finding.

SELECT
    verification_id,
    finding_id,
    reviewer_id,
    decision,
    evidence_assessment,
    verification_notes,
    verified_at
FROM grc_remediation_verifications
WHERE finding_id = ?
ORDER BY verification_id;