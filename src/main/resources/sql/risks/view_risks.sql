
-- ========== VIEWING MY REGISTERED RISKS ==========

-- I retrieve my saved risks and the employee responsible.
-- I use LEFT JOIN so risks without owners can still appear.

SELECT
    r.risk_id,
    r.title,
    r.description,
    r.likelihood,
    r.impact,
    r.risk_score,
    r.status,
    u.name AS owner_name
FROM risks r
LEFT JOIN users u
    ON r.owner_id = u.user_id
ORDER BY r.risk_id;
