
-- ============================================================
-- BANKGUARD v1.2 - VIEW FINDINGS
-- ============================================================

-- I retrieve the findings recorded in BankGuard.

SELECT
    finding_id,
    test_id,
    finding_title,
    finding_description,
    severity,
    owner_id,
    due_date,
    status,
    created_at
FROM grc_findings
ORDER BY finding_id DESC;
