
# BankGuard — GRC Management & Practical Learning Platform

**Version:** 1.2 — Findings, Remediation & Verification  
**Status:** Active Development — Release Candidate  
**Focus:** Governance, Risk & Compliance (GRC) | IT Risk | Banking | Business Systems

## About BankGuard

BankGuard is an independent Java-based Governance, Risk and Compliance (GRC) management and practical learning project developed around Ubuntu Digital Bank, a fictional South African financial institution.

I'm building BankGuard to apply my IT in Business Systems knowledge and develop practical skills in identifying, assessing, managing and monitoring technology risks.

My goal is to move beyond theoretical learning by building a system where I can practise realistic GRC responsibilities through simulated banking scenarios.

BankGuard combines **software development, business systems analysis, technology risk management and practical GRC learning**.

The long-term vision is to develop BankGuard into a web-based GRC platform and independent practical learning environment.

---

## Project Objectives

BankGuard has three main objectives:

1. **Build a functional GRC management system** that supports risk management, accountability, security controls, evidence management, independent reviews and remediation tracking.

2. **Practise GRC responsibilities** through fictional banking risk assessments, control testing and compliance projects.

3. **Demonstrate practical skills** by documenting how I design systems, evaluate controls, make risk-based decisions and continuously improve the platform.

---

## Current Features — v1.2

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
- Record the employee ID supplied for each change.
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

### 8. Independent Assessment Review & Approval — v1.1 Extension

BankGuard supports independent review of recorded control assessments.

This feature separates an analyst's assessment conclusion from a reviewer's approval decision.

**Features:**
- Approve or reject control assessments.
- Require a registered employee ID for the reviewer.
- Prevent analysts from reviewing their own assessments.
- Require supporting evidence references before approval.
- Record the reviewer's evaluation of the evidence.
- Require written reasons for review decisions.
- Prevent duplicate review decisions.
- Record reviewer IDs and timestamps.
- Retrieve saved review decisions.

Supported decisions:
- Approved
- Rejected

An approved review represents a recorded reviewer decision, not an independently certified audit conclusion.

### 9. Findings Register — New in v1.2

BankGuard now supports recording weaknesses identified during control assessments.

**Features:**
- Register findings against existing control assessments.
- Record finding titles and descriptions.
- Classify findings according to severity.
- Assign responsible employees.
- Set remediation deadlines.
- Track finding statuses.
- View registered findings.
- Validate finding information before saving.

Supported severity levels:
- Low
- Medium
- High
- Critical

**Example:**

A control assessment identifies that an employee can reset a customer's password using publicly available information.

The GRC analyst registers this weakness as a High-severity finding requiring corrective action.

### 10. Remediation Action Tracking — New in v1.2

BankGuard allows corrective actions to be assigned and tracked against identified findings.

**Features:**
- Register remediation actions.
- Link corrective actions to existing findings.
- Assign actions to registered employees.
- Set remediation deadlines.
- View actions linked to a finding.
- Record completion evidence references.
- Update corrective actions to Completed.
- Prevent duplicate completion.
- Prevent an employee ID different from the assigned employee from completing an action.
- Prevent completion of an action linked to a closed finding.

**Example:**

An IT employee is assigned to strengthen customer identity verification during account recovery.

After implementing the improvement, the employee records a fictional evidence reference demonstrating that the corrective work was completed.

### 11. Independent Remediation Verification — New in v1.2

BankGuard supports independent verification of corrective actions before a finding can be closed.

This separates the employee who performs corrective work from the employee who evaluates it.

**Features:**
- Record Verified or Rejected decisions.
- Require registered reviewers.
- Prevent finding owners from verifying their own findings.
- Prevent remediation assignees from verifying their own corrective work.
- Require completed corrective actions before a successful verification.
- Require completion evidence references.
- Record evidence assessments and review notes.
- Preserve verification history.
- Prevent duplicate successful verification decisions.

Supported decisions:
- Verified
- Rejected

**Example:**

After an IT employee strengthens the account recovery control, an independent reviewer evaluates the recorded remediation evidence.

If the corrective work is considered sufficient, the reviewer records a Verified decision with supporting reasons.

A successful verification does not automatically close the finding.

### 12. Formal Finding Closure — New in v1.2

BankGuard separates remediation completion, independent verification and formal finding closure.

Before allowing closure, the system checks that:

1. The finding exists and has not already been closed.
2. The employee ID supplied for closure is registered.
3. At least one remediation action exists.
4. All remediation actions are completed.
5. Each completed action has an evidence reference.
6. The latest verification decision is Verified.
7. Written closure justification is provided.

**Features:**
- Record formal closure decisions.
- Link closures to successful verification records.
- Preserve the employee ID supplied for closure.
- Require documented closure reasons.
- Record closure timestamps.
- Prevent duplicate closure.
- Save closure records and finding status changes in one database transaction.

**Governance principle:**

Corrective work should not be considered successfully resolved simply because someone says it has been completed.

The system should preserve evidence, independent verification and accountability before a finding is closed.

**Release-candidate note:** Additional controls are being added to prevent new remediation actions being registered after successful verification or closure.

---

## How the v1.2 GRC Workflow Works

```text
IDENTIFY AN IT RISK
        |
        v
REGISTER A SECURITY CONTROL
        |
        v
PERFORM A CONTROL ASSESSMENT
        |
        v
DOCUMENT TEST RESULTS & EVIDENCE
        |
        v
INDEPENDENT ASSESSMENT REVIEW
        |
        v
REGISTER A FINDING
        |
        v
ASSIGN REMEDIATION ACTIONS
        |
        v
COMPLETE CORRECTIVE ACTIONS
        |
        v
RECORD COMPLETION EVIDENCE
        |
        v
INDEPENDENT REMEDIATION VERIFICATION
        |
        +-----------------------+
        |                       |
     REJECTED                VERIFIED
        |                       |
        v                       v
FURTHER CORRECTIVE WORK   DOCUMENTED CLOSURE
        |                       |
        v                       v
REASSESS REMEDIATION      FINDING CLOSED
```

---

## Practical Banking GRC Scenario

### Customer Account Takeover — Weak Account Recovery

**Organisation:** Ubuntu Digital Bank (Fictional)

**Business Risk:** Customer Account Takeover

**Scenario:**

During control testing, a GRC analyst identifies that an employee can reset a customer's password using information that is publicly available.

This creates a weakness in the account recovery process and could increase the likelihood of unauthorised account access.

**Simulated GRC workflow:**

1. Register the account recovery weakness as a finding.
2. Classify the finding as High severity.
3. Assign responsibility to a registered employee.
4. Create corrective actions to strengthen identity verification.
5. Set remediation deadlines.
6. Record completion of the corrective work.
7. Document supporting evidence references.
8. Obtain an independent remediation verification decision.
9. Record formal closure reasons when the conditions are satisfied.
10. Preserve the resulting findings, remediation, verification and closure records.

**Learning outcomes:**
- Risk identification
- Control effectiveness assessment
- Findings management
- Accountability
- Remediation monitoring
- Evidence documentation
- Separation of duties
- Independent verification
- Risk governance
- Documented decision-making

All scenarios and records are simulated.

---

## Automated Testing & Quality Assurance

BankGuard uses JUnit and Maven to test application functionality.

### Automated Integration Tests

The v1.2 development milestone reached **59 passing automated tests**, covering:

- IT risk registration and scoring.
- Risk data validation.
- Risk ownership changes and audit history.
- Control registration.
- Control-to-risk relationships.
- Control assessment registration.
- Evidence registration.
- Independent assessment reviews.
- Findings registration and validation.
- Remediation action registration.
- Corrective action completion.
- Completion evidence requirements.
- Independent remediation verification.
- Prevention of self-verification.
- Verification history.
- Finding closure requirements.
- Prevention of duplicate finding closure.
- Preservation of closure records.

**Development testing milestone:**
- 59 automated tests passed locally.
- Zero failures.
- Zero errors.

The automated tests use temporary SQLite databases to keep test records separate from the normal application database.

### Final v1.2 Validation

The project is currently undergoing additional governance testing.

Planned tests include:
- Preventing new remediation actions after successful verification.
- Preventing actions being registered against closed findings.
- Allowing additional corrective work after a rejected verification.

These tests are expected to increase the total to **62 automated tests**.

The full end-to-end banking simulation is also pending.

### GitHub Actions

BankGuard uses GitHub Actions for continuous integration.

The workflow is configured to compile and test the project.

**GitHub Actions:**

https://github.com/siyabongamsengana/bankguard/actions

The latest v1.2 CI result must be confirmed separately from the successful local test run.

### Running Tests Locally

```bash
mvn clean test
```

Passing automated tests does not guarantee that the application has no defects or security weaknesses.

---

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Application logic |
| SQLite | Local database |
| SQL | Queries, relationships and migrations |
| JDBC | Database connectivity |
| Maven | Build and dependency management |
| JUnit 4 | Automated testing |
| Git | Version control |
| GitHub | Repository and development history |
| GitHub Actions | Continuous integration |
| Visual Studio Code | Development environment |

---

## System Architecture

BankGuard follows a layered architecture:

**Console UI → Services → Repositories → SQLite Database**

### Models

Represent application records, including:
- Risks
- Employees
- GRC projects
- Project tasks
- Controls
- Control tests
- Evidence
- Assessment reviews
- Findings
- Remediation actions
- Remediation verifications

### Services

Apply validation and business rules before data is saved.

### Repositories

Handle database operations, queries, transactions and record retrieval.

### SQL Resources

Store SQL queries and database migration instructions separately from Java code.

### Console Interface

Provides access to the risk management, GRC project, control assessment and findings management workspaces.

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

## Console Workspaces

BankGuard currently has three main workspaces.

### 1. GRC Project Workspace

Allows users to:
- Create GRC projects.
- Manage project tasks.
- Update task statuses.
- View project progress.

### 2. Control Assessment Workspace

Allows users to:
- Register security controls.
- Perform control assessments.
- Document supporting evidence references.
- Record independent assessment reviews.
- View assessment information.

### 3. Findings & Remediation Workspace — v1.2

Allows users to:
- Register findings.
- View the findings register.
- Create corrective actions.
- View remediation actions.
- Record corrective action completion.
- Perform independent remediation verification.
- View verification history.
- Close verified findings.

The Findings & Remediation Workspace is accessible through option 11 of the main console menu.

---

## Practical Banking GRC Projects

### Customer Account Takeover Risk Assessment

Assess risks relating to unauthorised customer account access.

Example controls:
- Multi-Factor Authentication
- Suspicious-login monitoring
- Phishing awareness training
- Account recovery verification

### PCI DSS Gap Analysis

A planned practical learning project involving selected PCI DSS requirements within a fictional payment environment.

Planned activities:
- Identify applicable requirements.
- Review simulated security controls.
- Identify compliance gaps.
- Document supporting evidence.
- Recommend corrective actions.
- Prepare assessment reports.

### Security Control Effectiveness Assessment

Evaluate whether security controls are working as intended.

Activities include:
- Reviewing control objectives.
- Documenting testing procedures.
- Recording observations.
- Evaluating simulated evidence.
- Recording assessment conclusions.
- Performing independent reviews.

### Additional Planned Assessment Areas

- Third-party technology risk.
- Phishing control effectiveness.
- AI governance.
- Information security compliance.
- IT risk and control self-assessments.

These are educational simulations. Not every listed assessment has been completed.

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
| v1.1 Extension | Independent assessment reviews | Completed |
| v1.2 | Findings, remediation, verification and closure | Implemented — final validation pending |
| v1.3 | Practical banking GRC simulations and workflow improvements | Planned |
| v1.4 | Assessment reporting and portfolio exports | Planned |
| Future | Spring Boot backend and web-based interface | Planned |

---

## Known Limitations

BankGuard is currently an educational prototype.

- Employee identities are not authenticated.
- Role-based access control has not yet been implemented.
- Evidence is represented by references rather than verified files.
- Verification relies on manually entered review information.
- Closure authority is not enforced through authenticated roles.
- The system requires additional safeguards against changes after successful verification.
- A formal finding reopening and re-verification process has not yet been implemented.
- SQLite is currently used as a local development database.
- The platform has not undergone professional security or compliance certification.

These limitations guide future development priorities.

---

## Future Vision

My long-term goal is to develop BankGuard into a web-based GRC workspace and practical learning environment.

I want BankGuard to support:

- Independent banking GRC assessments.
- IT risk and security control management.
- Evidence-based control testing.
- PCI DSS gap analysis.
- Findings and remediation tracking.
- Third-party risk assessments.
- AI governance assessments.
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
- Findings and remediation management.
- Independent review processes.
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

BankGuard is not a certified GRC, PCI DSS or compliance assessment product.

---

## Developer

**Siyabonga Msengana**

IT in Business Systems Student

**Areas of Interest:** Governance, Risk & Compliance | Technology Risk | Banking | PCI DSS | Information Security




