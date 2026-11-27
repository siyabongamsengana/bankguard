
-- ============================================================
-- BANKGUARD - ASSESSMENT REVIEW MIGRATION
-- ============================================================

-- I create a separate table for reviewing control tests.
-- This allows me to keep the analyst's original conclusion
-- separate from the reviewer's approval decision.

CREATE TABLE IF NOT EXISTS grc_assessment_reviews (

    -- I give each review a unique ID.
    review_id INTEGER PRIMARY KEY AUTOINCREMENT,

    -- I link the review to an existing control test.
    -- One test can have one final review decision.
    test_id INTEGER NOT NULL UNIQUE,

    -- I record the employee responsible for the review.
    reviewer_id INTEGER NOT NULL,

    -- I record whether the assessment was approved
    -- or rejected after the evidence was evaluated.
    decision TEXT NOT NULL
        CHECK (decision IN (
            'Approved',
            'Rejected'
        )),

    -- I record the reviewer's evaluation of the evidence.
    evidence_assessment TEXT NOT NULL
        CHECK (length(trim(evidence_assessment)) > 0),

    -- I preserve the reason for the review decision.
    review_notes TEXT NOT NULL
        CHECK (length(trim(review_notes)) > 0),

    -- I automatically record when the review took place.
    reviewed_at TEXT NOT NULL
        DEFAULT (strftime('%Y-%m-%dT%H:%M:%fZ', 'now')),

    -- I ensure the review belongs to a valid assessment.
    FOREIGN KEY (test_id)
        REFERENCES grc_control_tests(test_id),

    -- I ensure the reviewer exists in the employee register.
    FOREIGN KEY (reviewer_id)
        REFERENCES users(user_id)
);

-- I make it easier to find reviews by reviewer.

CREATE INDEX IF NOT EXISTS idx_assessment_reviewer
ON grc_assessment_reviews(reviewer_id);