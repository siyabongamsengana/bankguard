# BankGuard 🛡️
### IT Governance, Risk and Compliance Management System

BankGuard is a Java-based IT Governance, Risk and Compliance (GRC) project designed to explore how financial institutions can identify, assess, document and manage technology-related risks.

The project is based on a fictional organisation called **Ubuntu Digital Bank**.

As a Business Systems student interested in banking technology and GRC, I am developing BankGuard to apply what I learn about risk management, system development, internal controls and business processes.

**Project Status:** Early Development — Working Console Prototype (v0.3)

---

## Project Objectives

BankGuard aims to demonstrate how technology can support effective IT risk management.

The main objectives are to:

- Develop a structured IT risk register.
- Assess risks using likelihood and impact ratings.
- Support accountability through risk ownership.
- Improve the accuracy of recorded risk information.
- Explore the importance of evidence, audit trails and management approvals.
- Apply business analysis and GRC principles through software development.

## Current Features

The current prototype supports:

- **Risk Registration:** Users can enter and save IT risks.
- **Risk Assessment:** Risks are rated using likelihood and impact values from 1 to 5.
- **Automatic Risk Scoring:** SQLite calculates the risk score using likelihood × impact.
- **Input Validation:** Invalid likelihood and impact ratings are rejected.
- **Risk Register:** Users can retrieve and view saved risks.
- **Risk Ownership:** Risks can be linked to a fictional employee.
- **SQLite Integration:** Risk information is stored locally in a database.
- **Console Menu:** Users can register risks, view existing risks and exit the application.

These features are part of an early prototype and are not intended for use in a real banking environment.

## Technologies Used

| Technology | Purpose |
|---|---|
| Java | Application logic |
| SQLite | Database storage |
| JDBC | Connecting Java to SQLite |
| Maven | Project and dependency management |
| VS Code | Development environment |
| Git & GitHub | Version control and project documentation |

## Example Risk Assessment

**Organisation:** Ubuntu Digital Bank (Fictional)

**Risk:** Customer Account Takeover

**Description:** Criminals may gain unauthorised access to customer accounts.

| Risk Attribute | Value |
|---|---|
| Likelihood | 3/5 |
| Impact | 5/5 |
| Risk Score | 15/25 |
| Risk Owner | IT Operations Manager |

Risk Score = Likelihood × Impact

The example illustrates how BankGuard records risk assessment information.

## Planned Features

As development continues, I plan to introduce:

- Duplicate risk detection and prevention.
- Risk categories, statuses and treatment plans.
- Improved risk ownership and accountability.
- Role-based access control (RBAC).
- Audit trails and change history.
- Control assessments and evidence management.
- Risk acceptance and management approval workflows.
- Risk reporting and dashboards.
- Better user interfaces.

These are planned improvements and are not yet fully implemented.

## Running the Project Locally

**Requirements:**
- Java Development Kit (JDK 17 or later)
- Apache Maven

**Steps:**

1. Clone this repository.
2. Open the project folder containing `pom.xml`.
3. Compile the project using:

   `mvn compile`

4. Run `com.bankguard.App` from your Java-enabled IDE, such as VS Code.
5. Use the console menu to register and view IT risks.

The application creates its local SQLite database when it starts.

## What I'm Learning

Through BankGuard, I am developing practical understanding of:

- IT risk identification and assessment.
- Governance and accountability.
- Internal controls and input validation.
- Relational databases and SQL.
- Java programming and application design.
- Business requirements and system development.
- The relationship between technology and risk management in banking.

## Project Development

BankGuard is an ongoing educational and portfolio project.

My goal is not simply to build an application, but to understand how GRC requirements can be translated into functional systems and controls.

I welcome constructive feedback, suggestions and advice from people working in GRC, banking technology, software development and information security.

---

**Disclaimer:** BankGuard is an independent educational project. Ubuntu Digital Bank is fictional. The application is not affiliated with any financial institution and is not certified or approved as a regulatory compliance solution.
