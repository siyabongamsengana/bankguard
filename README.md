# BankGuard — IT Risk Management System

**Version:** 0.8 | **Status:** Under Development

## About the Project

BankGuard is a Java-based IT risk management system developed for Ubuntu Digital Bank, a fictional financial institution.

The project demonstrates how banks can identify, assess, document and manage technology risks while maintaining clear ownership and accountability.

I'm developing BankGuard as a practical portfolio project to apply my knowledge of Governance, Risk and Compliance (GRC), Business Systems and IT risk management.

## Key Features

### 1. Risk Registration
- Register IT risks with a title and description.
- Assess likelihood and impact on a scale of 1–5.
- Automatically calculate the risk score using likelihood × impact.
- Prevent duplicate risk titles.

### 2. Risk Monitoring
- View registered risks and their assessments.
- Track risk statuses: Open, In Progress, Mitigated and Closed.
- Update risk statuses as treatment progresses.

### 3. Employee Management
- Register employees with their names and roles.
- View registered employees and their unique IDs.
- Store employee information in an SQLite database.

### 4. Risk Ownership and Accountability
- Assign registered employees as risk owners.
- Prevent assigning risks to employees who do not exist.
- Transfer ownership of existing risks to another registered employee.
- View the current owner of each risk.

### 5. Data Storage
- Store risks and employees using SQLite.
- Preserve saved records between application sessions.
- Use SQL queries stored in separate resource files.

## Technologies Used

| Technology | Purpose |
|---|---|
| Java | Application development |
| SQLite | Database management |
| JDBC | Database connectivity |
| Maven | Project build and dependency management |
| Git and GitHub | Version control |
| VS Code | Development environment |

## Project Structure

```text
src/main/
├── java/com/bankguard/
│   ├── App.java
│   ├── database/
│   │   ├── DatabaseManager.java
│   │   └── SqlLoader.java
│   ├── model/
│   │   ├── Employee.java
│   │   └── Risk.java
│   ├── repository/
│   │   ├── EmployeeRepository.java
│   │   └── RiskRepository.java
│   ├── service/
│   │   ├── EmployeeService.java
│   │   └── RiskService.java
│   └── ui/
│       └── ConsoleMenu.java
└── resources/sql/
    ├── schema.sql
    ├── migrations/
    ├── risks/
    └── users/
```

## How Risk Scoring Works

BankGuard uses a simple quantitative scoring method:

**Risk Score = Likelihood × Impact**

Both likelihood and impact are scored from 1 to 5.

**Example: Customer Account Takeover**

- Likelihood: 3/5
- Impact: 5/5
- Risk score: 15/25

A higher score indicates a risk that requires greater attention. The score supports risk assessment and prioritisation; it does not replace professional judgement.

## How to Run the Project

**Requirements:**
- Java 17 or newer
- Apache Maven

Clone the repository:

```bash
git clone https://github.com/siyabongamsengana/bankguard.git
cd bankguard
```

Compile:

```bash
mvn clean compile
```

Run the application:

```bash
mvn exec:java -Dexec.mainClass=com.bankguard.App
```

The run command requires the Maven Exec Plugin to be available. Alternatively, run `App.java` from a Java-enabled IDE.

The application uses a local SQLite database, which is excluded from GitHub.

## Testing

BankGuard v0.8 successfully passed six functional tests:

- Employee registration
- Viewing registered employees
- Risk registration with a valid owner
- Rejection of invalid employee IDs
- Changing risk ownership
- Confirming updated risk ownership

**Result: 6/6 tests passed.**

## Development Progress

| Version | Feature | Status |
|---|---|---|
| v0.5 | Basic risk registration and assessment | Completed |
| v0.7 | Layered Java architecture and SQL resource separation | Completed |
| v0.8 | Employee management and risk ownership | Completed |
| v0.9 | Audit trails and change history | Planned |

## Future Improvements

The next phase will focus on audit trails, including recording risk ownership changes, risk status changes, previous and new values, timestamps and the employee reported as performing each action.

Longer-term improvements may include authentication, access controls, control assessments, evidence management and reporting.

## GRC Learning Objectives

Through BankGuard, I'm practising:

- IT risk identification and assessment.
- Risk ownership and accountability.
- Risk monitoring and treatment tracking.
- Database design and data integrity.
- Business rules and validation.
- Separation of responsibilities in software architecture.
- Documentation and functional testing.

## Disclaimer

BankGuard is an educational portfolio project. Ubuntu Digital Bank is fictional, and the application is not intended for production banking use.

---

**Developed by Siyabonga Msengana**

*IT in Business Systems | Governance, Risk and Compliance | Technology Risk*
