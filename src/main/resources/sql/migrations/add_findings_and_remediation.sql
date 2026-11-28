
-- ============================================================
-- BANKGUARD v1.2 - FINDINGS AND REMEDIATION MIGRATION
-- ============================================================

-- ============================================================
-- 1. FINDINGS REGISTER
-- ============================================================

-- I record weaknesses identified during control testing.
-- Each finding must be linked to an existing assessment.

CREATE TABLE IF NOT EXISTS grc_findings (

    -- I assign a unique ID to every finding.
    finding_id INTEGER PRIMARY KEY AUTOINCREMENT,

    -- I link the finding to the assessment
    -- that identified the weakness.
    test_id INTEGER NOT NULL,

    -- I record a meaningful name and description.
    finding_title TEXT NOT NULL
        CHECK (length(trim(finding_title)) > 0),

    finding_description TEXT NOT NULL
        CHECK (length(trim(finding_description)) > 0),

    -- I classify how serious the weakness is.
    severity TEXT NOT NULL
        CHECK (severity IN (
            'Low',
            'Medium',
            'High',
            'Critical'
        )),

    -- I record the employee responsible
    -- for resolving the finding.
    owner_id INTEGER NOT NULL,

    -- I record when remediation is expected.
    -- ISO date format: YYYY-MM-DD.
    due_date TEXT NOT NULL
        CHECK (
            due_date GLOB
            '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]'
            AND date(due_date) IS NOT NULL
            AND date(due_date) = due_date
        ),

    -- I track the finding through its lifecycle.
    status TEXT NOT NULL DEFAULT 'Open'
        CHECK (status IN (
            'Open',
            'In Progress',
            'Ready for Verification',
            'Closed'
        )),

    -- I preserve the original creation timestamp.
    created_at TEXT NOT NULL
        DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),

    -- I make sure linked records exist.
    FOREIGN KEY (test_id)
        REFERENCES grc_control_tests(test_id),

    FOREIGN KEY (owner_id)
        REFERENCES users(user_id)
);

-- ============================================================
-- 2. REMEDIATION ACTIONS
-- ============================================================

-- I allow one finding to have several corrective actions.

CREATE TABLE IF NOT EXISTS grc_remediation_actions (

    -- I assign a unique ID to every corrective action.
    action_id INTEGER PRIMARY KEY AUTOINCREMENT,

    -- I link each action to its finding.
    finding_id INTEGER NOT NULL,

    -- I describe what must be corrected.
    action_description TEXT NOT NULL
        CHECK (length(trim(action_description)) > 0),

    -- I assign responsibility for this specific action.
    assigned_to INTEGER NOT NULL,

    -- I record when the action should be finished.
    due_date TEXT NOT NULL
        CHECK (
            due_date GLOB
            '[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]'
            AND date(due_date) IS NOT NULL
            AND date(due_date) = due_date
        ),

    -- I track the progress of corrective work.
    status TEXT NOT NULL DEFAULT 'Open'
        CHECK (status IN (
            'Open',
            'In Progress',
            'Completed'
        )),

    -- I will use this field to reference
    -- supporting remediation evidence.
    completion_evidence TEXT,

    created_at TEXT NOT NULL
        DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),

    -- I protect the relationship between
    -- the action, finding and employee.
    FOREIGN KEY (finding_id)
        REFERENCES grc_findings(finding_id),

    FOREIGN KEY (assigned_to)
        REFERENCES users(user_id)
);

-- ============================================================
-- 3. DATABASE INDEXES
-- ============================================================

-- I retrieve findings linked to assessments quickly.
CREATE INDEX IF NOT EXISTS idx_findings_test
ON grc_findings(test_id);

-- I retrieve findings assigned to an employee.
CREATE INDEX IF NOT EXISTS idx_findings_owner
ON grc_findings(owner_id);

-- I retrieve findings by status and deadline.
CREATE INDEX IF NOT EXISTS idx_findings_status_due
ON grc_findings(status, due_date);

-- I retrieve corrective actions for a finding.
CREATE INDEX IF NOT EXISTS idx_remediation_finding
ON grc_remediation_actions(finding_id);

-- I retrieve actions assigned to employees.
CREATE INDEX IF NOT EXISTS idx_remediation_assignee
ON grc_remediation_actions(assigned_to);
