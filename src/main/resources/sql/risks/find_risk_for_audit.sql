
-- ============================================================
-- READING THE CURRENT RISK VALUES
-- ============================================================

-- I retrieve the current owner and status of a risk.
-- I need these values before making any changes.
-- This allows me to record what changed in my audit trail.

SELECT
    owner_id,
    status
FROM risks
WHERE risk_id = ?;