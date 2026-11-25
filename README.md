
# BankGuard — IT Risk Management System

**Version:** 0.9  
**Status:** Active Development  
**Focus:** Governance, Risk and Compliance (GRC) | IT Risk | Banking

## About BankGuard

BankGuard is a Java-based IT risk management system developed for Ubuntu Digital Bank, a fictional financial institution.

The project explores how organisations can identify, assess, monitor and manage technology risks while maintaining clear ownership, accountability and audit records.

I'm building BankGuard as a practical portfolio project to strengthen my understanding of Governance, Risk and Compliance (GRC), business systems and technology risk management within the banking sector.

Rather than only studying GRC concepts, I'm applying them by building a working system.

## Key Features

### 1. IT Risk Registration and Assessment
- Register IT risks with a title and description.
- Assess likelihood and impact using a 1–5 scale.
- Automatically calculate risk scores using likelihood × impact.
- Prevent duplicate risk titles.
- Assign each risk to a registered employee.

### 2. Risk Monitoring
- View registered risks, risk scores, owners and statuses.
- Update risk statuses: Open, In Progress, Mitigated and Closed.
- Monitor changes to existing risks.

### 3. Employee Management and Risk Ownership
- Register employees with their names and roles.
- View registered employees and their unique IDs.
- Assign risks to registered employees.
- Transfer risk ownership between employees.
- Prevent ownership assignments to employees who do not exist.

### 4. Audit Trails and Accountability — v0.9
- Automatically record changes to risk statuses and owners.
- Store the previous and new values.
- Record when changes occur using UTC timestamps.
- Record the employee ID supplied by the person making the change.
- View the audit history of a specific risk.
- Reject invalid acting employee IDs.
- Avoid creating audit entries when values remain unchanged.

### 5. Transaction Management
BankGuard uses SQLite database transactions to coordinate risk updates with their corresponding audit records.

If an audit insert fails, the application is designed to roll back the risk update so that both operations succeed or fail together.

This supports consistency between the risk register and its audit history.

## Technologies Used

| Technology | Purpose |
|---|---|
| Java | Application development and business logic |
| SQLite | Local database |
| JDBC | Database connectivity |
| Maven | Build and dependency management |
| SQL | Database queries and migrations |
| Git and GitHub | Version control |
| Visual Studio Code | Development environment |

## Project Architecture

BankGuard uses a layered structure to separate responsibilities.

```text
src/main/
├── java/com/bankguard/
│   ├── App.java
│   ├── database/
│   │   ├── DatabaseManager.java
│   │   └── SqlLoader.java
│   ├── model/
│   │   ├── Risk.java
│   │   ├── Employee.java
│   │   └── AuditLog.java
│   ├── repository/
│   │   ├── RiskRepository.java
│   │   ├── EmployeeRepository.java
│   │   └── AuditRepository.java
│   ├── service/
│   │   ├── RiskService.java
│   │   ├── EmployeeService.java
│   │   └── AuditService.java
│   └── ui/
│       └── ConsoleMenu.java
│
└── resources/sql/
    ├── schema.sql
    ├── migrations/
    │   ├── add_status.sql
    │   └── add_audit_logs.sql
    ├── risks/
    ├── users/
    └── audit/
        ├── insert_audit_log.sql
        └── view_audit_logs.sql
```

**Architecture responsibilities:**

- **Model:** Represents risks, employees and audit records.
- **Repository:** Communicates with SQLite.
- **Service:** Applies validation and business rules.
- **UI:** Provides the current console-based interface.
- **Database:** Manages connections, tables and migrations.

This separation makes the system easier to maintain, test and eventually convert into a web application.

## Risk Scoring

BankGuard calculates risk scores using:

**Risk Score = Likelihood × Impact**

Both values range from 1 to 5, producing scores between 1 and 25.

### Example: Customer Account Takeover

| Assessment | Value |
|---|---|
| Likelihood | 3/5 |
| Impact | 5/5 |
| Risk Score | 15/25 |

The score helps compare and prioritise risks. It is not a complete risk assessment on its own, and treatment decisions still require business context.

## Audit Trail Example

Suppose a risk owner changes from Employee 1 to Employee 2.

BankGuard records an entry similar to:

```text
Risk ID: R-1
Action: OWNER_CHANGED
Previous Value: 1
New Value: 2
Changed By Employee ID: 3
Timestamp (UTC): 2026-11-25T10:30:00.000Z
```

This allows the previous and new values to be reviewed rather than only displaying the latest information.

**Important limitation:** Employee IDs are currently self-reported. BankGuard does not yet authenticate the person performing the action, so these records should not be treated as verified user identities.

## How to Run BankGuard

### Requirements

- Java 17 or newer
- Apache Maven

### Clone the repository

```bash
git clone https://github.com/siyabongamsengana/bankguard.git
cd bankguard
```

### Compile the project

```bash
mvn clean compile
```

### Run the application

Open `App.java` in a Java-enabled IDE and run its `main` method.

BankGuard currently uses a console interface and creates a local SQLite database. Database files are excluded from version control.

## Functional Testing

### v0.8 — Risk Ownership

| Test | Result |
|---|---|
| Register an employee | Passed |
| View employees | Passed |
| Register a risk with an owner | Passed |
| Reject an invalid employee | Passed |
| Change risk ownership | Passed |
| Confirm updated ownership | Passed |

**Result: 6/6 passed.**

### v0.9 — Audit Trails

| Test | Result |
|---|---|
| Record a risk status change | Passed |
| View status change history | Passed |
| Record a risk ownership change | Passed |
| View ownership change history | Passed |
| Reject invalid acting employee | Passed |
| Reject unchanged values without creating new audit records | Passed |

**Result: 6/6 passed.**

The transaction implementation has also been reviewed for rollback handling. A deliberate failure test against the running Java application is still pending.

## Development Roadmap

| Version | Focus | Status |
|---|---|---|
| v0.5 | Basic risk registration and assessment | Completed |
| v0.7 | Layered architecture and SQL separation | Completed |
| v0.8 | Employee management and risk ownership | Completed |
| v0.9 | Audit trails and accountability | Completed |
| v1.0 | Risk reporting, risk categories and heat map | Planned |
| Future | Web dashboard, APIs, authentication and role-based access | Planned |
| Future | Control assessments, evidence management and risk treatment | Planned |

## Planned Web Application

The long-term goal is to convert BankGuard from a console application into a browser-based IT risk management system.

The planned architecture includes:

- **Frontend:** An interactive risk management dashboard.
- **Backend:** Java with Spring Boot and REST APIs.
- **Database:** SQLite initially, with potential migration to a server-based database.
- **Security:** Authentication and role-based access controls.
- **Reporting:** Risk summaries, heat maps and management reporting.

These are planned improvements, not features currently available in v0.9.

## What I'm Learning

Developing BankGuard is helping me build practical knowledge in:

- IT risk identification and assessment.
- Risk ownership and accountability.
- Risk monitoring and status tracking.
- Audit trails and traceability.
- Evidence retention and data integrity.
- Business rules and validation.
- Database design and transactions.
- Java application architecture.
- Translating GRC concepts into business system requirements.

The project is also helping me understand how technology can support risk management processes instead of treating GRC as only policies and documentation.

## Project Limitations

BankGuard is still an educational application.

Current limitations include:

- No user authentication or verified identity.
- No role-based access controls.
- No controls testing or evidence-upload functionality.
- No formal risk approval workflow.
- No protection against privileged users modifying the SQLite database directly.
- No web interface yet.

The audit trail is a useful learning implementation but should not be described as tamper-proof or production-grade.

## Disclaimer

BankGuard is an independent educational portfolio project.

Ubuntu Digital Bank is fictional. The application is not affiliated with any real financial institution and is not intended for production banking use.

---

**Developed by Siyabonga Msengana**

*IT in Business Systems Student | Governance, Risk and Compliance | Technology Risk*



