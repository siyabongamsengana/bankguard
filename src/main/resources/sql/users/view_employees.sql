
-- ============================================================
-- VIEWING MY REGISTERED EMPLOYEES
-- ============================================================

-- I retrieve all the employees saved in my database.
-- I want to see their IDs, names and roles.

-- I arrange them by employee ID so I can easily
-- identify employees when assigning risk owners.

SELECT
    user_id,
    name,
    role
FROM users
ORDER BY user_id;

