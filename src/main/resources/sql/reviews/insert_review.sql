
-- ============================================================
-- BANKGUARD - REGISTER ASSESSMENT REVIEW
-- ============================================================

-- I record the reviewer, decision and justification.

INSERT INTO grc_assessment_reviews (
    test_id,
    reviewer_id,
    decision,
    evidence_assessment,
    review_notes
)
VALUES (?, ?, ?, ?, ?);
