
-- ============================================================
-- RECORDING A CONTROL ASSESSMENT
-- ============================================================

-- I save a control test performed during an assessment.
-- I record my procedure, assessment period,
-- observations, conclusion and reported tester ID.

INSERT INTO grc_control_tests (
    control_id,
    test_procedure,
    assessment_period,
    test_results,
    conclusion,
    tested_by
)
VALUES (?, ?, ?, ?, ?, ?);
