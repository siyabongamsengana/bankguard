
-- ============================================================
-- REGISTERING CONTROL TEST EVIDENCE
-- ============================================================

-- I record an evidence item supporting a control test.
-- I save the evidence description, source and reference.

-- I do not upload actual files in this version.

INSERT INTO grc_evidence (
    test_id,
    evidence_name,
    evidence_source,
    evidence_description,
    evidence_reference
)
VALUES (?, ?, ?, ?, ?);
