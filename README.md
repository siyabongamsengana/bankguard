# BankGuard — GRC Management & Practical Learning Platform

**Version:** 1.0  
**Status:** Active Development  
**Focus:** Governance, Risk & Compliance (GRC) | IT Risk Management | Banking | Business Systems

## About BankGuard

BankGuard is a Java-based Governance, Risk and Compliance (GRC) project developed around Ubuntu Digital Bank, a fictional South African financial institution.

I'm building BankGuard to strengthen my practical understanding of how organisations identify, assess, manage and monitor technology risks while maintaining accountability and supporting auditability.

As an IT in Business Systems student with a strong interest in GRC within banking, I wanted to go beyond learning concepts from textbooks and online courses.

My goal is to build a system that allows me to practise GRC responsibilities through realistic fictional banking scenarios, document my decisions and develop a stronger understanding of how risk management works in practice.

**BankGuard is both a software development project and a self-directed GRC learning initiative.**

---

## Project Objectives

BankGuard has three main objectives:

**1. Develop a functional GRC management system**

Build a system that supports IT risk assessments, risk ownership, audit trails, GRC projects and eventually controls, compliance assessments, evidence and remediation management.

**2. Practise real-world GRC responsibilities**

Use simulated banking scenarios to practise identifying risks, evaluating controls, reviewing evidence, documenting findings and preparing recommendations.

**3. Document my professional development**

Maintain a portfolio of completed simulated GRC assessments that demonstrates my analytical thinking, technical knowledge, decision-making and willingness to learn independently.

---

## Current Features — v1.0

### 1. IT Risk Register

- Register IT risks with a title and description.
- Assess likelihood and impact using a 1–5 scale.
- Automatically calculate risk scores.
- Prevent duplicate risk titles.
- View registered risks, their owners and statuses.
- Update risk statuses.

**Risk Score = Likelihood × Impact**

For example, a risk with likelihood 3 and impact 5 receives a score of 15/25.

### 2. Employee Management & Risk Ownership

- Register employees with names and roles.
- View employee records.
- Assign registered employees as risk owners.
- Transfer risk ownership.
- Prevent ownership assignments to employees who do not exist.

This introduces accountability by connecting identified risks to responsible employees.

### 3. Audit Trails

- Record risk ownership changes.
- Record risk status changes.
- Preserve previous and new values.
- Record timestamps.
- Record the employee ID supplied by the person performing the change.
- View audit history for individual risks.

Risk updates and their corresponding audit records are processed using database transactions.

The system is designed to roll back the risk update if the audit insert fails.

**Limitation:** Employee IDs are currently self-reported because authentication has not yet been implemented.

### 4. GRC Project Workspace — New in v1.0

BankGuard now includes a dedicated workspace for managing practical GRC subprojects.

Current functionality includes:

- Create GRC projects.
- Categorise projects by GRC discipline.
- Store project descriptions and objectives.
- Create assessment tasks within projects.
- View tasks linked to individual projects.
- Update task statuses.
- Automatically calculate task completion percentages.
- Store project and task information in SQLite.

This provides the foundation for completing independent GRC assessments within BankGuard.

**Project progress currently measures task completion, not the quality or accuracy of an assessment.**

---

## Planned Practical GRC Subprojects

I intend to use BankGuard to complete several realistic banking GRC assessments.

### Project 01 — Customer Account Takeover Risk Assessment

Investigate the risk of unauthorised customer account access.

Planned activities include:

- Identifying threats and vulnerabilities.
- Reviewing MFA and login monitoring controls.
- Assessing inherent risk.
- Evaluating simulated control-testing evidence.
- Identifying control weaknesses.
- Assessing residual risk.
- Documenting recommendations.

### Project 02 — PCI DSS Gap Analysis

Assess selected PCI DSS requirements against a fictional banking payment environment.

Planned activities include:

- Defining the assessment scope.
- Identifying applicable requirements.
- Reviewing simulated control evidence.
- Documenting compliance gaps.
- Recommending corrective actions.
- Preparing a gap analysis report.

### Project 03 — Security Control Effectiveness Assessment

Evaluate whether selected information security controls are appropriately designed and operating effectively.

Planned activities include:

- Defining control objectives.
- Preparing test procedures.
- Reviewing simulated evidence.
- Documenting exceptions.
- Assessing control effectiveness.
- Preparing remediation recommendations.

### Other Planned Projects

- Third-party technology risk assessments.
- AI governance assessments.
- Access management reviews.
- IT risk and control self-assessments.
- Risk treatment and remediation tracking.

These are intended learning projects. Their inclusion here does not mean that all assessments have been completed.

---

## Technology Stack

| Technology | Purpose |
|---|---|
| Java | Core application and business logic |
| SQLite | Local database |
| JDBC | Database connectivity |
| SQL | Queries and database migrations |
| Maven | Dependency and build management |
| Git and GitHub | Version control |
| Visual Studio Code | Development environment |

### Application Architecture

BankGuard uses a layered Java architecture.

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
│   │   ├── AuditLog.java
│   │   ├── GrcProject.java
│   │   └── ProjectTask.java
│   ├── repository/
│   │   ├── RiskRepository.java
│   │   ├── EmployeeRepository.java
│   │   ├── AuditRepository.java
│   │   ├── GrcProjectRepository.java
│   │   └── ProjectTaskRepository.java
│   ├── service/
│   │   ├── RiskService.java
│   │   ├── EmployeeService.java
│   │   ├── AuditService.java
│   │   ├── GrcProjectService.java
│   │   └── ProjectTaskService.java
│   └── ui/
│       ├── ConsoleMenu.java
│       └── ProjectWorkspaceMenu.java
└── resources/sql/
    ├── schema.sql
    ├── migrations/
    ├── risks/
    ├── users/
    ├── audit/
    ├── projects/
    └── tasks/
```

**Models** represent application data.

**Repositories** manage database interactions.

**Services** apply business rules and validation.

**UI classes** manage interactions through the current console interface.

**Database classes** handle connections, schema creation and migrations.

---

## How to Run BankGuard

### Requirements

- Java 17 or newer
- Apache Maven
- A Java-enabled IDE, such as Visual Studio Code

### Clone the repository

```bash
git clone https://github.com/siyabongamsengana/bankguard.git
cd bankguard
```

### Compile the application

```bash
mvn clean compile
```

### Run the application

Open `src/main/java/com/bankguard/App.java` in your IDE and run its `main` method.

BankGuard currently uses a console-based interface.

The local SQLite database is excluded from version control.

---

## Development Roadmap

| Version | Development Focus | Status |
|---|---|---|
| v0.5 | Basic IT risk registration and scoring | Completed |
| v0.7 | Layered architecture and SQL separation | Completed |
| v0.8 | Employee management and risk ownership | Completed |
| v0.9 | Risk audit trails and accountability | Completed |
| v1.0 | GRC Project Workspace and task management | Implemented |
| v1.1 | Control register, evidence references and control testing | Planned |
| v1.2 | Findings and remediation tracking | Planned |
| v1.3 | Practical banking GRC simulation assignments | Planned |
| v1.4 | Reporting, assessment feedback and portfolio exports | Planned |
| Future | Web application with Spring Boot and interactive dashboards | Planned |

### Long-Term Vision

The goal is to transform BankGuard into a browser-based GRC workspace where I can:

- Manage simulated banking risks and controls.
- Work through independent GRC subprojects.
- Examine simulated evidence.
- Record assessment findings and recommendations.
- Receive structured feedback on my reasoning.
- Track my development across GRC disciplines.
- Produce professional assessment reports.

The planned web application will eventually provide dashboards for risks, controls, projects, evidence, findings and learning progress.

---

## Testing and Validation

BankGuard has undergone incremental development and functional testing.

**v0.8 — Risk Ownership:** Six functional tests passed.

**v0.9 — Audit Trails:** Six functional tests passed, including audit history retrieval and validation of acting employee IDs. A deliberate failure-and-rollback test against the running Java application remains pending.

**v1.0 — GRC Project Workspace:** Maven compilation completed successfully. Full end-to-end functional verification of project creation, task updates and progress tracking should be completed before marking the version fully tested.

Future improvements will include automated unit tests, integration tests and more formal test documentation.

---

## What I'm Learning Through BankGuard

This project is helping me develop practical knowledge in:

- IT risk identification and assessment.
- Risk scoring and prioritisation.
- Risk ownership and accountability.
- Audit trails and data integrity.
- Business systems analysis.
- Database design and validation.
- GRC project management.
- Java application architecture.
- Translating GRC requirements into system functionality.

As the project develops, I aim to expand into control testing, regulatory compliance, evidence assessment, remediation and management reporting.

I'm also learning that GRC is not simply about recording risks or meeting requirements. It involves understanding the business, evaluating evidence, communicating with stakeholders and supporting informed risk decisions.

---

## Project Limitations

BankGuard is currently an educational, console-based application.

It does not yet include:

- User authentication or role-based access control.
- A web interface.
- A control register and formal testing workflow.
- Evidence uploads or document management.
- Findings and remediation workflows.
- Production-grade audit-log protection.
- Automated assessment feedback.

All banking scenarios and records used for practice are fictional or simulated.

---

## Disclaimer

BankGuard is an independent educational portfolio project developed for practical learning.

Ubuntu Digital Bank is a fictional institution. The project is not affiliated with TymeBank or any other financial institution.

Simulated GRC assessments completed through BankGuard do not represent professional banking employment, actual compliance audits or independent certification of an organisation.

---

**Developed by Siyabonga Msengana**

*IT in Business Systems Student | Governance, Risk & Compliance | Technology Risk | Banking*
