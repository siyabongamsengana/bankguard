
-- ============================================================
-- REGISTERING AN IT CONTROL
-- ============================================================

-- I register a security or IT control.
-- I link it to an existing risk and control owner.
-- SQLite generates the control ID automatically.

INSERT INTO grc_controls (
    risk_id,
    control_name,
    control_objective,
    control_type,
    owner_id,
    frequency
)
VALUES (?, ?, ?, ?, ?, ?);
