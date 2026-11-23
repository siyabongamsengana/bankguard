# BankGuard 🛡️
### IT Governance, Risk and Compliance Management System

BankGuard is a Java-based IT Governance, Risk and Compliance (GRC) project focused on identifying, assessing, recording and managing technology risks within the banking sector.

The system is being developed around **Ubuntu Digital Bank**, a fictional financial institution.

As a Business Systems student interested in banking GRC and technology risk, I'm building BankGuard to apply my understanding of risk management, internal controls, business processes and software development.

**Current Version:** v0.5 — Risk Status Management  
**Project Status:** In Development

---

## Project Objectives

The aim of BankGuard is to explore how technology can support effective IT risk management in financial institutions.

The project focuses on:

- Identifying and registering technology risks.
- Assessing likelihood and impact.
- Maintaining a structured IT risk register.
- Improving data accuracy through validation controls.
- Supporting accountability through risk ownership.
- Tracking risk management activities.
- Exploring how governance requirements can be implemented through software.

## Current Features

### 1. IT Risk Registration
Users can register IT risks by entering their names, descriptions, likelihood ratings and impact ratings.

### 2. Risk Assessment and Scoring
Risks are assessed using a 1–5 scale for likelihood and impact.

**Risk Score = Likelihood × Impact**

The maximum score is 25. SQLite automatically calculates the score.

### 3. Input Validation
BankGuard rejects incomplete risk information and ratings outside the accepted 1–5 range.

### 4. Duplicate Risk Detection
The application checks for existing risk titles before registering new risks, helping reduce duplicate records.

### 5. Risk Ownership
Risks are linked to an employee through a database relationship. The current prototype uses a fictional default risk owner.

### 6. Risk Status Management
The system supports four risk statuses:

- **Open:** Risk has been registered.
- **In Progress:** Risk treatment activities are underway.
- **Mitigated:** Risk treatment has reduced the risk.
- **Closed:** Risk has been marked as closed.

Users can update risk statuses through the console menu. Formal approval and evidence requirements are planned for future development.

### 7. SQLite Database Integration
Risk records are saved locally and can be retrieved after restarting the application.

### 8. Interactive Console Menu
Users can register risks, view existing records, update risk statuses and exit the system.

---

## Technologies Used

| Technology | Purpose |
|---|---|
| Java | Application development and business logic |
| SQLite | Local database management |
| JDBC | Java and database communication |
| Maven | Project and dependency management |
| VS Code | Development environment |
| Git & GitHub | Version control and documentation |

## Example Risk Assessment

**Fictional Organisation:** Ubuntu Digital Bank

**Risk:** Customer Account Takeover

**Description:** Criminals may gain unauthorised access to customer banking accounts.

| Attribute | Value |
|---|---|
| Likelihood | 3/5 |
| Impact | 5/5 |
| Risk Score | 15/25 |
| Risk Owner | IT Operations Manager |
| Initial Status | Open |

This example demonstrates how BankGuard captures and stores IT risk information.

## Running BankGuard Locally

**Requirements:**
- JDK 17 or later
- Apache Maven

**Instructions:**

1. Clone the GitHub repository.
2. Open the project folder containing `pom.xml`.
3. Run `mvn compile` to compile the project.
4. Open `App.java` in a Java-enabled IDE such as VS Code.
5. Run the application.
6. Use the console menu to interact with BankGuard.

The application creates a local SQLite database if one does not already exist.

## Development Roadmap

### Completed Development
- [x] Java and SQLite integration
- [x] Database table creation
- [x] IT risk registration
- [x] Automatic risk scoring
- [x] Input validation
- [x] Risk register retrieval
- [x] Basic duplicate risk detection
- [x] Interactive console menu
- [x] Basic risk status management

### Planned Improvements
- [ ] Stronger database-level duplicate prevention
- [ ] Multiple employee registration and risk assignment
- [ ] Risk categories and treatment plans
- [ ] Audit trails and change history
- [ ] Role-based access control (RBAC)
- [ ] Control testing and evidence management
- [ ] Risk approval and escalation workflows
- [ ] Risk acceptance and closure authorisation
- [ ] Risk reporting and dashboards
- [ ] Graphical user interface

## What I'm Learning

Developing BankGuard is helping me connect my Business Systems studies with practical GRC principles.

The project is improving my understanding of:

- IT risk management and assessment
- Governance and accountability
- Preventive controls and data validation
- Business analysis and system requirements
- Relational databases and SQL
- Java programming
- Risk management workflows
- Technology risk within financial institutions

One of my main learning objectives is understanding how risk management requirements can be translated into working system controls.

## Feedback and Contributions

BankGuard is an ongoing personal learning project.

I welcome constructive feedback, technical suggestions and insights from people working in GRC, IT risk, banking technology, business analysis and software development.

## Disclaimer

BankGuard is an independent educational and portfolio project. Ubuntu Digital Bank is fictional.

The system is not designed for production banking use and is not certified as a regulatory compliance solution.
