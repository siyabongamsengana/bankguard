
-- ============================================================
-- CREATING A GRC PROJECT
-- ============================================================

-- I create a new practical GRC assessment project.
-- SQLite automatically generates the project ID.
-- I use the default status of Planned.

INSERT INTO grc_projects (
    project_name,
    description,
    category
)
VALUES (?, ?, ?);
