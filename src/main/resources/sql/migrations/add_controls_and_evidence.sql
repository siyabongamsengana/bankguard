
-- ============================================================
-- BANKGUARD v1.1 - CONTROLS AND EVIDENCE
-- ============================================================

-- I create a register of security and IT controls.
-- Each control must be linked to an existing risk.

CREATE TABLE IF NOT EXISTS grc_controls (

    -- I assign a unique ID to every control.
    control_id INTEGER PRIMARY KEY AUTOINCREMENT,

    -- I link the control to a risk in my risk register.
    risk_id INTEGER NOT NULL,

    -- I record the control's name and purpose.
    control_name TEXT NOT NULL
        CHECK (length(trim(control_name)) > 0),

    control_objective TEXT NOT NULL
        CHECK (length(trim(control_objective)) > 0),

    -- I classify how the control manages risk.
    control_type TEXT NOT NULL
        CHECK (control_type IN (
            'Preventive',
            'Detective',
            'Corrective'
        )),

    -- I record who is responsible for the control.
    owner_id INTEGER NOT NULL,

    -- I record how often the control operates.
    frequency TEXT NOT NULL,

    -- I track whether the control is active.
    status TEXT NOT NULL DEFAULT 'Active'
        CHECK (status IN (
            'Active',
            'Inactive',
            'Under Review'
        )),

    created_at TEXT NOT NULL
        DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),

    FOREIGN KEY (risk_id)
        REFERENCES risks(risk_id),

    FOREIGN KEY (owner_id)
        REFERENCES users(user_id)
);

-- ============================================================
-- CONTROL TESTING
-- ============================================================

-- I record assessments of my registered controls.
-- One control can have multiple tests over time.

CREATE TABLE IF NOT EXISTS grc_control_tests (

    test_id INTEGER PRIMARY KEY AUTOINCREMENT,

    -- I identify the control being assessed.
    control_id INTEGER NOT NULL,

    -- I describe how the control was tested.
    test_procedure TEXT NOT NULL
        CHECK (length(trim(test_procedure)) > 0),

    -- I record the period covered by the test.
    assessment_period TEXT NOT NULL,

    -- I record the evidence-based observations.
    test_results TEXT NOT NULL,

    -- I distinguish effective controls from controls
    -- with weaknesses or insufficient evidence.
    conclusion TEXT NOT NULL DEFAULT 'Not Tested'
        CHECK (conclusion IN (
            'Not Tested',
            'Effective',
            'Ineffective',
            'Inconclusive'
        )),

    -- I record the employee ID supplied by the tester.
    tested_by INTEGER NOT NULL,

    tested_at TEXT NOT NULL
        DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),

    FOREIGN KEY (control_id)
        REFERENCES grc_controls(control_id),

    FOREIGN KEY (tested_by)
        REFERENCES users(user_id)
);

-- ============================================================
-- EVIDENCE REGISTER
-- ============================================================

-- I record references to the evidence I review.
-- This version stores descriptions and references,
-- not the actual evidence files.

CREATE TABLE IF NOT EXISTS grc_evidence (

    evidence_id INTEGER PRIMARY KEY AUTOINCREMENT,

    -- I link evidence to a particular control test.
    test_id INTEGER NOT NULL,

    -- I give each evidence item a meaningful name.
    evidence_name TEXT NOT NULL
        CHECK (length(trim(evidence_name)) > 0),

    -- I record where the evidence came from.
    evidence_source TEXT NOT NULL,

    -- I describe what the evidence demonstrates.
    evidence_description TEXT NOT NULL,

    -- I keep a reference for future review.
    -- This can be a fictional document identifier.
    evidence_reference TEXT NOT NULL,

    recorded_at TEXT NOT NULL
        DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),

    FOREIGN KEY (test_id)
        REFERENCES grc_control_tests(test_id)
);

-- ============================================================
-- INDEXES
-- ============================================================

-- I make it easier to retrieve controls for each risk.
CREATE INDEX IF NOT EXISTS idx_controls_risk
ON grc_controls(risk_id);

-- I make it easier to retrieve tests for each control.
CREATE INDEX IF NOT EXISTS idx_tests_control
ON grc_control_tests(control_id);

-- I make it easier to find evidence for a control test.
CREATE INDEX IF NOT EXISTS idx_evidence_test
ON grc_evidence(test_id);