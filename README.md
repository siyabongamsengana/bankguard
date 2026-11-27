
# BankGuard — GRC Management & Practical Learning Platform

**Version:** 1.1 — Assessment Review Extension  
**Status:** Active Development  
**Focus:** Governance, Risk & Compliance (GRC) | IT Risk | Banking | Business Systems

## About BankGuard

BankGuard is an independent Java-based Governance, Risk and Compliance (GRC) management and practical learning project developed around Ubuntu Digital Bank, a fictional South African financial institution.

I'm building BankGuard to apply my IT in Business Systems knowledge and develop practical skills in identifying, assessing, managing and monitoring technology risks.

My goal is to go beyond theoretical learning by building a system where I can practise GRC responsibilities through realistic, simulated banking scenarios.

BankGuard combines **software development, business systems analysis, risk management and practical GRC learning**.

The long-term vision is to develop BankGuard into a web-based GRC platform and independent practical learning environment.

---

## Project Objectives

BankGuard has three main objectives:

1. **Build a functional GRC management system** that supports risk management, accountability, security controls, evidence management, assessment reviews and compliance activities.

2. **Practise GRC responsibilities** through fictional banking risk assessments, control testing and compliance projects.

3. **Demonstrate practical skills** by documenting how I design systems, evaluate controls, make risk-based decisions and improve the platform.

---

## Current Features — v1.1

### 1. IT Risk Register

BankGuard allows users to register and manage technology risks.

**Features:**
- Register and describe IT risks.
- Assess likelihood and impact using a 1–5 scale.
- Automatically calculate risk scores.
- Assign risks to registered employees.
- Update risk statuses.
- View registered risks and their owners.
- Validate risk information before saving it.

**Risk Score = Likelihood × Impact**

Risk scores range from 1 to 25.

Supported risk statuses:
- Open
- In Progress
- Mitigated
- Closed

### 2. Employee Management & Accountability

BankGuard maintains employee records to support risk ownership and accountability.

**Features:**
- Register employees and their roles.
- View employee records.
- Assign employees as risk owners.
- Change risk ownership.
- Validate employee IDs before assigning responsibilities.

Employee IDs are currently entered manually and do not represent authenticated user sessions.

### 3. Risk Audit Trails

BankGuard records changes made to risk ownership and risk status.

**Features:**
- Record risk ownership changes.
- Record risk status changes.
- Preserve previous and updated values.
- Record the employee ID supplied for the change.
- Automatically record timestamps.
- View audit history for individual risks.
- Use database transactions to keep risk updates and related audit records consistent.

This supports accountability and traceability within the simulated GRC environment.

### 4. GRC Project Workspace — v1.0

The Project Workspace allows users to organise different GRC activities.

**Features:**
- Create GRC projects.
- Categorise assessment projects.
- Add tasks to projects.
- Update task statuses.
- Track project completion percentages.
- Store projects and tasks in SQLite.

Example project categories include:
- IT Risk Assessments
- PCI DSS Compliance
- Security Control Testing
- Third-Party Risk
- AI Governance

### 5. Control Register — v1.1

BankGuard allows users to document IT and security controls linked to identified risks.

**Features:**
- Register security and IT controls.
- Link controls to existing risks.
- Record control objectives.
- Assign control owners.
- Record operating frequency.
- Record control status.
- View controls linked to individual risks.

Supported control types:
- Preventive
- Detective
- Corrective

Examples include Multi-Factor Authentication, suspicious-login monitoring and account recovery verification.

### 6. Control Testing — v1.1

BankGuard supports recording assessments of registered security controls.

**Features:**
- Record control-testing procedures.
- Define assessment periods.
- Document test observations and results.
- Record proposed assessment conclusions.
- Link assessments to registered controls.
- View control-testing history.
- Record the employee ID supplied by the tester.

Supported conclusions:
- Not Tested
- Effective
- Ineffective
- Inconclusive

A recorded conclusion is not automatically an approved conclusion.

### 7. Evidence Register — v1.1

BankGuard supports documenting evidence references associated with control assessments.

**Features:**
- Register evidence references.
- Link evidence to control tests.
- Record evidence names and sources.
- Document what the evidence demonstrates.
- Preserve document reference numbers.
- View evidence linked to an assessment.

**Current limitation:** BankGuard stores evidence descriptions and references rather than actual evidence files.

The existence of an evidence reference does not prove that the underlying evidence is authentic, reliable or sufficient.

### 8. Assessment Review & Approval Workflow

BankGuard supports independent review of recorded control assessments.

This feature separates an analyst's assessment conclusion from a reviewer's approval decision.

**Features:**
- Approve or reject control assessments.
- Require a registered employee ID for the reviewer.
- Prevent analysts from reviewing their own assessments.
- Require at least one supporting evidence reference before approval.
- Record the reviewer's evaluation of the evidence.
- Require written reasons for review decisions.
- Prevent duplicate review decisions for the same assessment.
- Record reviewer IDs and timestamps.
- Retrieve saved review decisions.

**Review decisions:**
- Approved
- Rejected

**Example workflow:**

1. An analyst records an MFA control assessment.
2. The analyst documents test observations and a proposed conclusion.
3. Supporting evidence references are registered.
4. A different registered employee reviews the assessment.
5. The reviewer records an approval or rejection with written reasons.
6. BankGuard saves the review record for future reference.

**Current limitations:**
- Reviewer identities are not authenticated.
- Evidence quality is evaluated through manually entered review information.
- Actual evidence files are not independently verified.
- Each assessment currently supports one review decision.
- Rejected assessments cannot yet be resubmitted through a versioned workflow.

An approved review represents a recorded reviewer decision, not an independently certified audit conclusion.

---

## Automated Testing & Quality Assurance

BankGuard uses JUnit and Maven to test application functionality.

### Automated Integration Tests

The current automated test suite contains **19 tests** covering:

- Risk score calculation.
- Invalid risk rating validation.
- Valid control registration.
- Invalid control type validation.
- Control-to-risk relationships.
- Control owner validation.
- Control assessment registration.
- Invalid assessment conclusions.
- Evidence registration.
- Invalid evidence relationships.
- Risk status audit logging.
- Approval with supporting evidence.
- Rejection of approval without evidence.
- Prevention of self-review.
- Prevention of unregistered reviewers.
- Rejection decisions without evidence.
- Prevention of duplicate review decisions.
- Required review reasoning.
- Invalid review decision validation.

**Verified testing milestone:**
- 19 automated tests passed.
- Zero failures.
- Zero errors.
- GitHub Actions completed successfully.

The tests use temporary SQLite databases to keep test records separate from the normal application database.

Passing automated tests does not guarantee the absence of defects. Additional security, performance and user-interface testing remain ongoing.

### GitHub Actions

BankGuard uses GitHub Actions for continuous integration.

Whenever changes are pushed to the main branch or a pull request targets main, the workflow:

1. Checks out the repository.
2. Configures Java 17.
3. Compiles the project using Maven.
4. Runs the automated tests.
5. Uploads test results.

**Workflow:** `.github/workflows/maven-tests.yml`

View automated test results:

https://github.com/siyabongamsengana/bankguard/actions

### Running Tests Locally

```bash
mvn clean test
```

Maven runs the JUnit tests and reports whether they passed or failed.

---

## Practical Banking GRC Projects

BankGuard is being developed to support realistic learning activities involving Ubuntu Digital Bank.

### Customer Account Takeover Risk Assessment

Assess the risk of unauthorised access to customer accounts.

Example controls:
- Multi-Factor Authentication
- Suspicious-login monitoring
- Phishing awareness training
- Account recovery verification

Learning areas:
- Risk identification
- Likelihood and impact assessment
- Control effectiveness
- Residual risk
- Evidence documentation
- Escalation and accountability

### PCI DSS Gap Analysis

Review selected PCI DSS requirements against a fictional payment environment.

Planned activities:
- Identify applicable requirements.
- Review simulated security controls.
- Identify compliance gaps.
- Document supporting evidence.
- Recommend corrective actions.
- Prepare assessment reports.

### Security Control Effectiveness Assessment

Evaluate how well security controls manage identified technology risks.

Activities include:
- Reviewing control objectives.
- Documenting testing procedures.
- Recording observations.
- Evaluating simulated evidence.
- Recording proposed conclusions.
- Performing independent assessment reviews.

### Additional Planned Assessment Areas

- Third-party technology risk.
- Phishing control effectiveness.
- AI governance.
- Information security compliance.
- IT risk and control self-assessments.
- Findings and remediation management.

These are educational simulations. Not every listed assessment has been completed.

---

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Application logic |
| SQLite | Local database |
| SQL | Queries, relationships and migrations |
| JDBC | Database connectivity |
| Maven | Build and dependency management |
| JUnit 4 | Automated integration testing |
| Git | Version control |
| GitHub | Repository and project history |
| GitHub Actions | Continuous integration and testing |
| Visual Studio Code | Development environment |

---

## System Architecture

BankGuard follows a layered architecture:

**Console UI → Services → Repositories → SQLite Database**

### Models

Represent application records, including:
- Risks
- Employees
- Audit logs
- GRC projects
- Project tasks
- Controls
- Control tests
- Evidence
- Assessment reviews

### Services

Apply validation and business rules before information is saved.

### Repositories

Handle database operations and retrieve saved records.

### SQL Resources

Store SQL queries and database migration instructions separately from Java code.

### Console Interface

Allows users to interact with risk management, project management, control testing, evidence management and assessment review functions.

This architecture supports maintainability and future development.

---

## Running BankGuard

### Requirements

- Java 17 or newer
- Apache Maven
- A Java development environment

### Clone the Repository

```bash
git clone https://github.com/siyabongamsengana/bankguard.git
```

### Open the Project

```bash
cd bankguard
```

### Compile

```bash
mvn clean compile
```

### Run the Application

Open:

`src/main/java/com/bankguard/App.java`

Run its `main` method using your Java development environment.

BankGuard currently uses a console interface and a local SQLite database.

### Run Automated Tests

```bash
mvn clean test
```

---

## Development Roadmap

| Version | Development Focus | Status |
|---|---|---|
| v0.5 | IT risk registration and scoring | Completed |
| v0.7 | Layered architecture and SQL separation | Completed |
| v0.8 | Employee management and risk ownership | Completed |
| v0.9 | Risk audit trails | Completed |
| v1.0 | GRC Project Workspace | Completed |
| v1.1 | Control registration, testing and evidence references | Completed |
| v1.1 Extension | Assessment reviews and approval decisions | Implemented and automated tests passed |
| v1.2 | Findings and remediation tracking | Planned |
| v1.3 | Practical banking GRC simulations | Planned |
| v1.4 | Assessment reporting and portfolio exports | Planned |
| Future | Spring Boot backend and web-based interface | Planned |

---

## Next Development Phase — Findings & Remediation

The next BankGuard milestone is Findings and Remediation Tracking.

The goal is to connect weaknesses discovered during control testing to corrective actions and accountability.

Planned functionality:
- Register control weaknesses as findings.
- Link findings to risks, controls and assessments.
- Classify findings by severity.
- Assign responsible employees.
- Set remediation deadlines.
- Track corrective actions.
- Record remediation evidence.
- Verify whether corrective actions were effective.
- Preserve a history of remediation decisions.
- Prevent unsupported finding closure.

**Example:**

An account recovery control fails because an employee resets a customer password using information publicly available online.

BankGuard should allow the analyst to document the weakness, assign an owner, record corrective actions and verify remediation before the finding is closed.

---

## Future Vision

My long-term goal is to develop BankGuard into a web-based GRC workspace and practical learning environment.

I want BankGuard to support:

- Independent banking GRC assessments.
- IT risk and control management.
- Evidence-based control testing.
- Compliance gap analysis.
- Findings and remediation tracking.
- Assessment approval workflows.
- Practical GRC assignments.
- Structured feedback on decisions.
- Professional assessment reporting.
- Portfolio evidence generation.
- Personal learning progress tracking.

The purpose is to develop both the technical system and the professional reasoning required for GRC work.

---

## What I'm Learning

Through BankGuard, I'm developing practical knowledge in:

- IT risk assessment and management.
- Governance and accountability.
- Security control design and testing.
- Evidence evaluation and documentation.
- Assessment review processes.
- Audit trails and data integrity.
- Business systems analysis.
- Database design.
- Java programming.
- SQL and JDBC.
- Software testing.
- GitHub Actions and continuous integration.
- GRC workflows and decision-making.

I'm particularly interested in how financial institutions manage technology risk, protect payment systems and support responsible decision-making through effective governance and security controls.

---

## Project Disclaimer

BankGuard is an independent educational and portfolio project.

Ubuntu Digital Bank is a fictional financial institution.

All banking scenarios are simulated and do not represent actual banking employment or professional audit engagements.

BankGuard is not a certified GRC or compliance assessment product.

---

## Developer

**Siyabonga Msengana**

IT in Business Systems Student

**Areas of Interest:** Governance, Risk & Compliance | Technology Risk | Banking | PCI DSS | Information Security


