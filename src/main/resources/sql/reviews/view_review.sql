
-- ============================================================
-- BANKGUARD - VIEW ASSESSMENT REVIEW
-- ============================================================

-- I retrieve the review linked to a control test.

SELECT
    review_id,
    test_id,
    reviewer_id,
    decision,
    evidence_assessment,
    review_notes,
    reviewed_at
FROM grc_assessment_reviews
WHERE test_id = ?;
