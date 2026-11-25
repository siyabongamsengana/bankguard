
-- ============================================================
-- VIEWING TASKS FOR ONE PROJECT
-- ============================================================

-- I retrieve the tasks linked to a selected project.

SELECT
    task_id,
    project_id,
    task_name,
    description,
    status,
    created_at
FROM grc_project_tasks
WHERE project_id = ?
ORDER BY task_id ASC;
