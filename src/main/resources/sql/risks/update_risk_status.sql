
-- ========== UPDATING MY RISK STATUS ==========

-- I update the status of the risk with the selected ID.
-- My Java program checks whether the status is valid.

UPDATE risks
SET status = ?
WHERE risk_id = ?;
