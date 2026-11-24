
-- ============================================================
-- REGISTERING A NEW IT RISK
-- ============================================================

-- I use this instruction to register a new IT risk.

-- I store the risk name, description, likelihood,
-- impact and the employee responsible for the risk.

-- I give every new risk an Open status.

-- SQLite will automatically calculate my risk score.

INSERT INTO risks (
    title,
    description,
    likelihood,
    impact,
    owner_id,
    status
)
VALUES (?, ?, ?, ?, ?, 'Open');
