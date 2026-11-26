# BankGuard — GRC Management & Practical Learning Platform

**Version:** 1.1  
**Status:** Active Development  
**Focus:** Governance, Risk & Compliance (GRC) | IT Risk | Banking | Business Systems

## About BankGuard

BankGuard is an independent Java-based Governance, Risk and Compliance (GRC) project developed around Ubuntu Digital Bank, a fictional South African financial institution.

I'm building BankGuard to apply my IT in Business Systems knowledge and develop practical skills in identifying, assessing, managing and monitoring technology risks.

The goal is to go beyond theoretical learning by creating a platform where I can practise GRC responsibilities through realistic, simulated banking scenarios.

BankGuard combines **software development, risk management and practical GRC learning**.

## Project Objectives

The project has three main objectives:

1. **Build a functional GRC management system** that supports risk management, accountability, controls, evidence and compliance activities.
2. **Practise GRC responsibilities** by completing simulated banking risk assessments, control reviews and compliance projects.
3. **Document my development** through practical projects that demonstrate my analytical thinking, technical knowledge and understanding of GRC processes.

---

## Current Features — v1.1

### 1. IT Risk Register

- Register and describe IT risks.
- Assess likelihood and impact on a 1–5 scale.
- Automatically calculate risk scores.
- Assign registered employees as risk owners.
- Update risk statuses.
- View and monitor registered risks.

**Risk Score = Likelihood × Impact**

### 2. Employee Management & Accountability

- Register employees and their roles.
- View employee records.
- Assign and change risk ownership.
- Validate employee IDs before assigning responsibilities.

### 3. Risk Audit Trails

- Record risk ownership changes.
- Record risk status changes.
- Preserve previous and updated values.
- Record timestamps and reported acting employee IDs.
- View audit history for individual risks.
- Use database transactions to keep risk updates and corresponding audit records consistent.

### 4. GRC Project Workspace — v1.0

- Create and manage GRC projects.
- Categorise different assessment projects.
- Add tasks to individual projects.
- Track task statuses.
- Calculate project completion percentages.
- Store projects and tasks in SQLite.

This workspace helps organise independent GRC assessments.

### 5. Control Register — New in v1.1

- Register security and IT controls.
- Link controls to existing risks.
- Record control objectives.
- Classify controls as Preventive, Detective or Corrective.
- Assign registered employees as control owners.
- Record operating frequency and control status.
- View all controls or filter them by risk.

### 6. Control Testing — New in v1.1

- Record control-testing procedures.
- Define assessment periods.
- Document observations and test results.
- Record assessment conclusions.
- Link tests to registered controls.
- View previous control assessments.

Supported conclusions:

- Not Tested
- Effective
- Ineffective
- Inconclusive

**Current limitation:** Conclusions are recorded assessments, not independently approved audit findings. The system does not yet require evidence review before recording a conclusion.

### 7. Evidence Register — New in v1.1

- Register evidence references linked to control tests.
- Document evidence names, sources and descriptions.
- Store document references for assessment records.
- Retrieve evidence associated with a particular control test.

The current version records evidence descriptions and references. It does not upload, verify or securely store actual evidence files.

---

## Practical Banking GRC Projects

BankGuard is being developed to support realistic simulations involving Ubuntu Digital Bank.

### Customer Account Takeover Risk Assessment

Assess the risk of unauthorised customer account access, including MFA, suspicious-login monitoring and account recovery controls.

### PCI DSS Gap Analysis

Review selected PCI DSS requirements against a fictional payment environment, identify compliance gaps and document recommendations.

### Security Control Effectiveness Assessment

Examine simulated evidence, evaluate security controls, identify weaknesses and document control-testing conclusions.

### Future Assessment Areas

- Third-party technology risk
- Phishing control effectiveness
- AI governance
- Information security compliance
- IT risk and control self-assessments
- Remediation management

These are intended practical learning activities. Not all assessments have been completed.

---

## Technology Stack

| Technology | Purpose |
|---|---|
| Java | Application logic |
| SQLite | Local database |
| SQL | Queries, records and migrations |
| JDBC | Database connectivity |
| Maven | Build and dependency management |
| Git & GitHub | Version control |
| Visual Studio Code | Development environment |

### Architecture

BankGuard follows a layered architecture:

**Console UI → Services → Repositories → SQLite Database**

- **Models:** Represent risks, employees, projects, controls, tests and evidence.
- **Services:** Apply validation and business rules.
- **Repositories:** Manage database operations.
- **SQL Resources:** Separate database queries from Java code.
- **Console UI:** Provides access to the application's functions.

This structure helps make the code easier to maintain and extend.

---

## Running BankGuard

### Requirements

- Java 17 or newer
- Apache Maven
- A Java development environment

### Clone the repository

`git clone https://github.com/siyabongamsengana/bankguard.git`

Open the project folder:

`cd bankguard`

### Compile

`mvn clean compile`

### Run

Open `src/main/java/com/bankguard/App.java` in your IDE and run its `main` method.

BankGuard currently uses a console interface and a local SQLite database.

---

## Development Roadmap

| Version | Focus | Status |
|---|---|---|
| v0.5 | IT risk registration and scoring | Completed |
| v0.7 | Layered architecture and SQL separation | Completed |
| v0.8 | Employee management and risk ownership | Completed |
| v0.9 | Risk audit trails | Completed |
| v1.0 | GRC Project Workspace | Implemented |
| v1.1 | Control register, control testing and evidence references | Implemented |
| v1.2 | Findings and remediation tracking | Planned |
| v1.3 | Practical banking GRC simulations | Planned |
| v1.4 | Assessment reporting and portfolio exports | Planned |
| Future | Spring Boot backend and web-based interface | Planned |

**Version 1.1 has compiled successfully and has been pushed to GitHub. Comprehensive functional and automated testing is still pending.**

---

## Future Vision

My long-term goal is to turn BankGuard into a web-based GRC workspace and practical learning environment.

I want the platform to support:

- Independent banking GRC assessments.
- Evidence-based control testing.
- IT risk management.
- Compliance gap analyses.
- Findings and remediation tracking.
- Structured feedback on assessment decisions.
- Professional GRC reporting.
- Personal learning progress tracking.

The goal is to develop both the technical system and the professional reasoning needed for GRC work.

---

## What I'm Learning

Through BankGuard, I'm developing knowledge and skills in:

- IT risk assessment and management.
- Risk ownership and accountability.
- Security control design and testing.
- Evidence documentation.
- Audit trails and data integrity.
- Business systems analysis.
- Database design.
- Java programming.
- GRC workflows.
- Technical problem-solving.

I'm particularly interested in understanding how banks use technology, governance and security controls to manage risk and support responsible decision-making.

---

## Project Disclaimer

BankGuard is an independent educational and portfolio project.

Ubuntu Digital Bank is a fictional financial institution. All practical banking scenarios are simulated and do not represent actual banking employment or professional audit engagements.

The application is not a certified GRC or compliance assessment product.

---

**Developed by Siyabonga Msengana**

*IT in Business Systems Student | Governance, Risk & Compliance | Technology Risk | Banking*


