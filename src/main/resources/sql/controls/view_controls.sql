
-- ============================================================
-- VIEWING MY CONTROL REGISTER
-- ============================================================

-- I retrieve the controls recorded in BankGuard.
-- I display their risks, owners and operating details.

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
ORDER BY control_id ASC;
