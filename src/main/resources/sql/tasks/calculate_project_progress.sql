
-- ============================================================
-- CALCULATING MY PROJECT PROGRESS
-- ============================================================

-- I count all tasks linked to a project.
-- I also count how many of those tasks are completed.

-- I use conditional aggregation to calculate
-- completion percentage in Java later.

SELECT
    COUNT(*) AS total_tasks,
    COUNT(CASE
        WHEN status = 'Completed' THEN 1
    END) AS completed_tasks
FROM grc_project_tasks
WHERE project_id = ?;
