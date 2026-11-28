
-- ============================================================
-- BANKGUARD v1.2 - UPDATE FINDING STATUS
-- ============================================================

-- I close only the finding selected for approval.
-- My repository checks the supporting conditions first.

UPDATE grc_findings
SET status = 'Closed'
WHERE finding_id = ?
AND status <> 'Closed';