
-- ============================================================
-- BANKGUARD v1.2 - REGISTER FINDING
-- ============================================================

-- I save a weakness linked to a control assessment.

INSERT INTO grc_findings (
    test_id,
    finding_title,
    finding_description,
    severity,
    owner_id,
    due_date
)
VALUES (?, ?, ?, ?, ?, ?);
