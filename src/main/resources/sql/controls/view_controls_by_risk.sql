
-- ============================================================
-- VIEWING CONTROLS FOR ONE RISK
-- ============================================================

-- I retrieve controls linked to a selected risk.
-- This helps me understand how the risk is managed.

SELECT
    control_id,
    risk_id,
    control_name,
    control_objective,
    control_type,
    owner_id,
    frequency,
    status,
    created_at
FROM grc_controls
WHERE risk_id = ?
ORDER BY control_id ASC;
