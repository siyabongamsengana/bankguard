
-- ========== CHECKING FOR DUPLICATE RISKS ==========

-- I search for an existing risk with the same title.
-- I ignore capital letters and spaces around the title.

SELECT COUNT(*)
FROM risks
WHERE LOWER(TRIM(title)) = LOWER(TRIM(?));
