
-- ============================================================
-- BANKGUARD DATABASE SCHEMA
-- ============================================================

-- ========== USERS TABLE ==========

-- I create this table to store employees
-- and their roles within my fictional bank.

CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    role TEXT NOT NULL
);

-- ========== RISKS TABLE ==========

-- I create this table to store IT risks.

-- I limit likelihood and impact to values from 1 to 5.

-- I calculate the risk score automatically.

-- I give every new risk an Open status.

-- I link each risk to an employee using owner_id.

CREATE TABLE IF NOT EXISTS risks (
    risk_id INTEGER PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT NOT NULL,

    likelihood INTEGER NOT NULL
        CHECK (likelihood BETWEEN 1 AND 5),

    impact INTEGER NOT NULL
        CHECK (impact BETWEEN 1 AND 5),

    risk_score INTEGER GENERATED ALWAYS AS
        (likelihood * impact) VIRTUAL,

    owner_id INTEGER,

    status TEXT NOT NULL DEFAULT 'Open'
        CHECK (status IN (
            'Open',
            'In Progress',
            'Mitigated',
            'Closed'
        )),

    FOREIGN KEY (owner_id)
        REFERENCES users(user_id)
);