
package com.bankguard.repository;

// ============================================================
// IMPORTS
// ============================================================

// I use these classes to save and retrieve employees
// from my SQLite database.

import com.bankguard.database.DatabaseManager;
import com.bankguard.database.SqlLoader;
import com.bankguard.model.Employee;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRepository {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    // I store the locations of my employee SQL queries.

    private static final String REGISTER_EMPLOYEE_FILE =
            "/sql/users/register_employee.sql";

    private static final String VIEW_EMPLOYEES_FILE =
            "/sql/users/view_employees.sql";

    private static final String FIND_EMPLOYEE_FILE =
            "/sql/users/find_employee.sql";


    // ========================================================
    // 2. REGISTERING AN EMPLOYEE
    // ========================================================

    public void saveEmployee(String name, String role)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;


        // ========== INITIALISATION ==========

        // I load the SQL instruction for registration.
        sql = SqlLoader.loadSql(REGISTER_EMPLOYEE_FILE);


        // ========== PROCESSING ==========

        // I send the employee's information to SQLite.
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, role);

            // I save the employee record.
            stmt.executeUpdate();
        }
    }


    // ========================================================
    // 3. VIEWING MY EMPLOYEES
    // ========================================================

    public List<Employee> findAllEmployees()
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        List<Employee> employees;
        String sql;
        Employee employee;


        // ========== INITIALISATION ==========

        // I create a list for the employees I retrieve.
        employees = new ArrayList<>();

        sql = SqlLoader.loadSql(VIEW_EMPLOYEES_FILE);


        // ========== PROCESSING ==========

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql);
             ResultSet results = stmt.executeQuery()) {

            // I go through every employee record.
            while (results.next()) {

                employee = new Employee(
                        results.getInt("user_id"),
                        results.getString("name"),
                        results.getString("role")
                );

                employees.add(employee);
            }
        }


        // ========== OUTPUT ==========

        // I return the employee list.
        return employees;
    }


    // ========================================================
    // 4. CHECKING WHETHER AN EMPLOYEE EXISTS
    // ========================================================

    public boolean employeeExists(int employeeId)
            throws SQLException, IOException {

        // ========== DECLARATIONS ==========

        String sql;
        boolean exists;


        // ========== INITIALISATION ==========

        exists = false;

        sql = SqlLoader.loadSql(FIND_EMPLOYEE_FILE);


        // ========== PROCESSING ==========

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement stmt =
                     conn.prepareStatement(sql)) {

            // I search using the selected employee ID.
            stmt.setInt(1, employeeId);

            try (ResultSet results = stmt.executeQuery()) {

                // A returned row means the employee exists.
                exists = results.next();
            }
        }


        // ========== OUTPUT ==========

        return exists;
    }
}
