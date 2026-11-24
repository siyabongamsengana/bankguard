
package com.bankguard.model;

public class Employee {

    // ========================================================
    // 1. DECLARATIONS
    // ========================================================

    // I store the information about an employee.
    private final int employeeId;
    private final String name;
    private final String role;


    // ========================================================
    // 2. INITIALISING MY EMPLOYEE
    // ========================================================

    // I use this constructor to create an employee
    // using the information stored in SQLite.
    public Employee(int employeeId, String name, String role) {

        this.employeeId = employeeId;
        this.name = name;
        this.role = role;
    }


    // ========================================================
    // 3. GETTING EMPLOYEE INFORMATION
    // ========================================================

    // I use these methods when I want to read
    // the employee's information.

    public int getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }
}
