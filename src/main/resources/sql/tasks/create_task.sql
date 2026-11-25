
-- ============================================================
-- CREATING A GRC PROJECT TASK
-- ============================================================

-- I add a task to an existing GRC project.
-- SQLite automatically generates the task ID.
-- A new task starts with Not Started status.

INSERT INTO grc_project_tasks (
    project_id,
    task_name,
    description
)
VALUES (?, ?, ?);
