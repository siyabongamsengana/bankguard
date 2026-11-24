
package com.bankguard.service;

// ============================================================
// IMPORTS
// ============================================================

// I use my repository for employee database operations.

import com.bankguard.model.Employee;
import com.bankguard.repository.EmployeeRepository;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class EmployeeService {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    private final EmployeeRepository employeeRepository;


    // ========================================================
    // 2. INITIALISATION
    // ========================================================

    public EmployeeService() {

        // I prepare my employee repository.
        employeeRepository = new EmployeeRepository();
    }


    // ========================================================
    // 3. REGISTERING A NEW EMPLOYEE
    // ========================================================

    public String registerEmployee(String name, String role) {

        // ========== VALIDATION ==========

        // I don't allow employees without a name or role.
        if (name == null || name.isBlank()
                || role == null || role.isBlank()) {

            return "Employee name and role are required.";
        }


        // ========== PROCESSING ==========

        try {

            // I remove unnecessary surrounding spaces.
            employeeRepository.saveEmployee(
                    name.trim(),
                    role.trim()
            );


            // ========== OUTPUT ==========

            return "Employee registered successfully!";

        } catch (SQLException | IOException e) {

            return "Employee registration failed: "
                    + e.getMessage();
        }
    }


    // ========================================================
    // 4. RETRIEVING MY EMPLOYEES
    // ========================================================

    public List<Employee> getAllEmployees()
            throws SQLException, IOException {

        // I retrieve employees from my database.
        return employeeRepository.findAllEmployees();
    }


    // ========================================================
    // 5. VALIDATING AN EMPLOYEE ID
    // ========================================================

    public boolean employeeExists(int employeeId)
            throws SQLException, IOException {

        // I reject IDs that cannot identify an employee.
        if (employeeId <= 0) {

            return false;
        }

        // I ask the repository to check my database.
        return employeeRepository.employeeExists(employeeId);
    }
}

