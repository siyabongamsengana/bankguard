
-- ============================================================
-- BANKGUARD v1.0 - GRC PROJECT MANAGEMENT
-- ============================================================

-- I create a table to store my GRC projects.
-- Each project represents an assessment or investigation
-- I want to complete as part of my practical GRC learning.

CREATE TABLE IF NOT EXISTS grc_projects (

    -- I give every project a unique ID.
    project_id INTEGER PRIMARY KEY AUTOINCREMENT,

    -- I record the name of my GRC project.
    project_name TEXT NOT NULL
        CHECK (length(trim(project_name)) > 0),

    -- I explain what the project aims to achieve.
    description TEXT NOT NULL,

    -- I classify the type of GRC work.
    -- Examples include IT Risk Assessment,
    -- PCI DSS Compliance and Control Testing.
    category TEXT NOT NULL,

    -- I track the project's current progress.
    status TEXT NOT NULL DEFAULT 'Planned'
        CHECK (status IN (
            'Planned',
            'In Progress',
            'Completed',
            'On Hold'
        )),

    -- I automatically record when I create a project.
    created_at TEXT NOT NULL
        DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now'))
);

-- ============================================================
-- PROJECT TASKS
-- ============================================================

-- I create another table for the tasks inside each project.
-- One project can have many tasks.

CREATE TABLE IF NOT EXISTS grc_project_tasks (

    -- I give each task its own unique ID.
    task_id INTEGER PRIMARY KEY AUTOINCREMENT,

    -- I connect the task to its parent GRC project.
    project_id INTEGER NOT NULL,

    -- I record what needs to be done.
    task_name TEXT NOT NULL
        CHECK (length(trim(task_name)) > 0),

    -- I describe the work expected for this task.
    description TEXT NOT NULL DEFAULT '',

    -- I track whether the task has been completed.
    status TEXT NOT NULL DEFAULT 'Not Started'
        CHECK (status IN (
            'Not Started',
            'In Progress',
            'Completed'
        )),

    -- I record when I create the task.
    created_at TEXT NOT NULL
        DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),

    -- I make sure the task belongs to an existing project.
    FOREIGN KEY (project_id)
        REFERENCES grc_projects(project_id)
        ON DELETE RESTRICT
);

-- ============================================================
-- INDEXES
-- ============================================================

-- I create an index to make it easier to retrieve
-- all the tasks belonging to a particular project.

CREATE INDEX IF NOT EXISTS idx_grc_tasks_project_id
ON grc_project_tasks(project_id);