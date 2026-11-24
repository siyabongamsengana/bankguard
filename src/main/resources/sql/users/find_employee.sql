
-- ============================================================
-- CHECKING IF AN EMPLOYEE EXISTS
-- ============================================================

-- I check whether an employee is registered in my bank.
-- I search for the employee using their unique ID.

-- I do this before assigning or transferring risk ownership.
-- A risk should only be assigned to a registered employee.

-- The question mark is replaced by the employee ID
-- provided by my Java application.

SELECT
    user_id
FROM users
WHERE user_id = ?;
