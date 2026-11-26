
-- ============================================================
-- VIEWING EVIDENCE FOR A CONTROL TEST
-- ============================================================

-- I retrieve evidence references linked to a test.
-- This allows me to review what information
-- supports the recorded assessment.

SELECT
    evidence_id,
    test_id,
    evidence_name,
    evidence_source,
    evidence_description,
    evidence_reference,
    recorded_at
FROM grc_evidence
WHERE test_id = ?
ORDER BY evidence_id ASC;
