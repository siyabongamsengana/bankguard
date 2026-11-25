
-- ============================================================
-- CHECKING WHETHER A PROJECT EXISTS
-- ============================================================

-- I use a project ID to check whether a project exists.
-- This prevents me from assigning tasks to missing projects.

SELECT project_id
FROM grc_projects
WHERE project_id = ?;
