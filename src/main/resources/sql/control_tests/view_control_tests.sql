
-- ============================================================
-- VIEWING PREVIOUS CONTROL TESTS
-- ============================================================

-- I retrieve assessments for a particular control.
-- I arrange them by test ID so I can review
-- the control's assessment history.

SELECT
    test_id,
    control_id,
    test_procedure,
    assessment_period,
    test_results,
    conclusion,
    tested_by,
    tested_at
FROM grc_control_tests
WHERE control_id = ?
ORDER BY test_id ASC;
