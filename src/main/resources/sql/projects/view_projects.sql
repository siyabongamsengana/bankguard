
-- ============================================================
-- VIEWING MY GRC PROJECTS
-- ============================================================

-- I retrieve all my projects and their information.
-- I display the newest projects first.

SELECT
    project_id,
    project_name,
    description,
    category,
    status,
    created_at
FROM grc_projects
ORDER BY project_id DESC;
