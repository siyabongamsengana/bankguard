
-- ============================================================
-- REGISTERING A NEW EMPLOYEE
-- ============================================================

-- I register a new employee in my bank's database.
-- I save the employee's name and their job role.

-- I do not enter the employee ID myself.
-- SQLite automatically generates a unique employee ID.

-- The question marks are placeholders.
-- My Java EmployeeRepository provides the actual values.

INSERT INTO users (
    name,
    role
)
VALUES (
    ?,
    ?
);
