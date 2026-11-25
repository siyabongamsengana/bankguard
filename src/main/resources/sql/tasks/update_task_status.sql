
-- ============================================================
-- UPDATING A PROJECT TASK
-- ============================================================

-- I update the status of a selected task.
-- I check both task ID and project ID to avoid
-- changing a task that belongs to another project.

UPDATE grc_project_tasks
SET status = ?
WHERE task_id = ?
  AND project_id = ?;
