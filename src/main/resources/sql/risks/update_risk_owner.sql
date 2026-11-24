
-- ============================================================
-- UPDATING RISK OWNERSHIP
-- ============================================================

-- I assign an existing risk to a different employee.
-- The first question mark is the NEW employee ID.
-- The second question mark is the EXISTING risk ID.

UPDATE risks
SET owner_id = ?
WHERE risk_id = ?;
