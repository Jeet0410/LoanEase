# LoanEase Project Report

## Table of Contents
1. [Introduction](#Introduction)  
2. [Design Problem](#Design-Problem)  
    2.1 [Problem Definition](#problem-definition)  
    2.2 [Design Requirements](#design-requirements)  
         2.2.1 [Functions](#functions)  
         2.2.2 [Objectives](#objectives)  
         2.2.3 [Constraints](#constraints)  
3. [Solution](#solution)  
   3.1 [Solution 1](#31-solution-1)  
   3.2 [Solution 2](#32-solution-2)  
   3.3 [Final Solution](#33-final-solution)  
        3.3.1 [Components](#components)  
        3.3.2 [Features](#features)  
        3.3.3 [Environmental, Societal, Safety, and Economic Considerations](#environmental-societal-safety-and-economic-considerations)  
        3.3.4 [Limitations](#limitations) 
4. [Team Work](#team-work)  
    4.1 [Meeting 1](#meeting-1)  
    4.2 [Meeting 2](#meeting-2)  
    4.3 [Meeting 3](#meeting-3)  
    4.4 [Meeting 4](#meeting-4)  
5. [Project Management](#project-management)  
6. [Conclusion and Future Work](#conclusion-and-future-work)  
7. [References](#references)  
8. [Appendix](#appendix)

## Introduction
LoanEase is a user-friendly **loan amortization simulator** designed to demystify the true cost of borrowing. It lets users input loan parameters, explore *what‑if* scenarios (such as extra payments or rate shocks), and instantly view detailed repayment schedules and visualisations.

**Rationale.** Consumers often receive only static tables from lenders. These do not reveal how rounding policies, pre‑payments, or interest‑rate changes alter total interest paid or payoff time. By providing an interactive, open‑source tool that follows rigorous software‑testing practices, LoanEase empowers users to make informed financial decisions while serving as a demonstrative case study for ENSE 375’s testing techniques.

This report follows the structure specified in the course template. Section 2 defines the design problem; later sections will document alternative solutions, final architecture, testing, teamwork, and project-management artifacts.

## Design Problem

### Problem Definition
Millions of consumers struggle to understand the long-term cost of loans and the impact of extra payments or changes in interest rates. Financial institutions usually provide static amortization tables that do not reveal how rounding rules, payment timing, or rate fluctuations affect total interest paid or payoff time.

The goal of **LoanEase** is to build a modular, open-source **loan amortization simulator** that:

* **Lets users model** standard and customized repayment schedules.  
* **Runs what-if scenarios** (e.g., extra payments, rate shocks).  
* **Exports clear tables and charts** that highlight the real cost of borrowing.  

By delivering accurate, scenario-driven insights through a friendly interface—and by being developed with rigorous software-testing practices—LoanEase empowers borrowers to make evidence-based financial decisions and serves as a practical case study for ENSE 375’s testing techniques.

### Design Requirements

#### Functions
1. **LoadLoanParameters** – ingest principal, nominal interest rate, term, payment frequency, and optional extra-payment plan.  
2. **GenerateSchedule** – compute per-period principal, interest, and remaining balance using the chosen amortization method.  
3. **RunScenario** – adjust one or more parameters (e.g., extra payment, interest-rate shock) and recompute the schedule.  
4. **ExportSchedule** – save the schedule as CSV and PDF.  
5. **VisualizeSchedule** – render line/bar charts of balance over time and interest vs. principal components.  
6. **ValidateInputs** – enforce numeric bounds and detect invalid configurations before calculation.  

*Extra functions inherited from the **FinancialCalculator** super-class include* `roundCurrency`, `computePMT`, `interestPortion`, and `principalPortion`.

#### Objectives
- **Accurate** – results match authoritative financial formulas within ± 0.01 CAD.  
- **Reliable** – handles edge cases (0 % rate, 0-month term) without crashing.  
- **Extensible** – new financial tools can inherit from `FinancialCalculator` and add domain-specific logic.  
- **Usable** – clear GUI/CLI with immediate feedback and export options.  
- **Maintainable** – clean, layered architecture with > 90 % unit-test coverage and self-documenting code.  

#### Constraints

| ID | Constraint | Category | Binary Measure |
|----|------------|----------|----------------|
| C1 | Use only open-source libraries (Apache-2.0, MIT) | Economic | ✅ dependency audit passes |
| C2 | Conform to Canadian APR rounding rules | Regulatory compliance | ✅ sample cases match CRA guidelines |
| C3 | Schedule generation completes in < 1 s for a 40-year term (480 periods) on a mid-range laptop | Reliability | ✅ benchmark ≤ 1 s |
| C4 | Exported files contain no personally identifiable information | Ethics/Security | ✅ static scan shows none |
| C5 | Runs on any JDK 17+ without additional installs | Sustainability/Deployment | ✅ launch script succeeds |

## Solution

The engineering design followed an **iterative, prototype-driven approach**. Each iteration added functionality and testability while revealing shortcomings that informed the next round. Testing was a core consideration, aligning with the Software Testing and Validation course requirements, using test-driven development (TDD) and JUnit, with techniques like path testing, data flow, integration testing, boundary value testing, equivalence class testing, decision tables testing, state transition testing, and use case testing.

### 3.1 Solution 1 – Web-Based Proof-of-Concept
| Aspect | Description |
|--------|-------------|
| **Implementation** | A basic single-page web application using HTML, CSS, and vanilla JavaScript (`index.html`, `app.js`). Users input loan parameters via a form, and the app calculates and displays a simple amortization table using client-side logic. |
| **Purpose** | Validate the feasibility of a web-based interface and test core financial calculations (e.g., PMT, interest/principal split) in a browser environment before adding complexity. |
| **Testing Focus** | Initial TDD with Jasmine for numeric helpers; manual testing of form inputs and table outputs; boundary value tests for edge cases (0% rate, 1-month term). |
| **Strengths** | ✔ Quick to develop (≈3 days) <br> ✔ Accessible via any browser for broad testing <br> ✔ Simple TDD setup with Jasmine allowed early validation of financial formulas |
| **Weaknesses** | ✖ Limited test coverage—no support for path or data flow testing due to monolithic design <br> ✖ Lack of integration testing between UI and logic <br> ✖ No automated testing for state transitions or use cases, relying on manual checks |
| **Reason Not Selected** | Insufficient testability and scalability; the monolithic structure hindered comprehensive testing required by the course, necessitating a more modular design. |

### 3.2 Solution 2 – Mobile App Prototype
| Aspect | Description |
|--------|-------------|
| **Implementation** | A lightweight mobile app prototype using Flutter (`main.dart`) targeting Android and iOS. Users input loan details, and the app generates a basic amortization schedule with a simple line chart using the `fl_chart` package. Data is stored locally using SQLite. |
| **Independence from S1** | Independent codebase, exploring a mobile-first approach to broaden accessibility beyond web users. |
| **Testing Focus** | TDD with the `test` package for financial calculations; widget tests for UI components; integration tests for local storage and chart rendering; basic boundary value and equivalence class testing. |
| **Strengths** | ✔ Native mobile experience with offline support <br> ✔ TDD and widget testing enabled early detection of UI and logic issues <br> ✔ Integration testing with SQLite provided confidence in data persistence |
| **Weaknesses** | ✖ Limited cross-platform testing resources constrained state transition and use case testing <br> ✖ Lack of decision tables testing due to absent complex logic branches <br> ✖ App store deployment added testing overhead, reducing focus on optimal test suites |
| **Reason Not Final** | Testing complexity and lack of web accessibility, combined with incomplete coverage of required testing techniques, motivated a return to a CLI-based solution with robust testing (Section 3.3). |

### 3.3 Final Solution – Modular CLI-Based Architecture

The final design delivers LoanEase as a **modular Java-based Command-Line Interface (CLI) application**, developed using test-driven development (TDD) and JUnit to create optimal test suites. This solution prioritizes maintainability and testability, allowing users to run it locally on any JDK 17+ environment without additional installs, and includes export capabilities for CSV and PDF.

#### 3.3.1 Components
| # | Component | Technology | Purpose | Key Tests |
|---|-----------|------------|---------|-----------|
| 1 | **LoanCLI** | Java with SLF4J | Main entry point handling user input via Scanner and orchestrating loan calculations. | • JUnit tests with path and data flow testing <br> • Use case testing for input flows |
| 2 | **Formula Engine** | Shared Java module (`financial-calculator`) | Houses `FinancialCalculator`, `LoanModel`, and utilities like `roundCurrency`, `computePMT`. | • Path, data-flow, and boundary value tests <br> • Equivalence class testing for input ranges |
| 3 | **AmortizationService** | Java | Generates amortization schedules based on loan parameters. | • Integration tests with MockMvc <br> • State transition testing for schedule updates |
| 4 | **ExportUtil** | Java with Apache PDFBox & CSV | Handles export of schedules to CSV and PDF files. | • Decision-table tests on export formats <br> • Boundary value testing for file operations |
| 5 | **LoanInputValidator** | Java | Validates loan parameters (e.g., principal, rate, term). | • Boundary value and equivalence class testing <br> • Integration tests with LoanCLI |
| 6 | **Persistence Layer** | SQLite (Spring Data) | Stores and retrieves loan data locally. | • Repository integration tests with boundary value testing |

#### 3.3.2 Features
* **Interactive CLI Form** – real-time input validation and prompt-based navigation  
* **Scenario Management** – allows adjustment of parameters (e.g., extra payments, rate shocks) and recomputation  
* **Schedule Output** – displays detailed repayment schedules in the terminal  
* **Export** – saves schedules as CSV and PDF files  
* **Session Persistence** – saves and loads loan data using SQLite  
* **Error Handling** – provides clear feedback for invalid inputs or failures  

#### 3.3.3 Environmental, Societal, Safety, and Economic Considerations
| Factor | Mitigation / Positive Impact |
|--------|-----------------------------|
| **Economic** | Open-source license avoids vendor lock-in; CLI reduces hardware dependency |
| **Regulatory / Security** | No network transmission; local SQLite stores no PII; file exports comply with C4 |
| **Reliability** | >80% test coverage with JUnit; CI pipeline ensures stability |
| **Sustainability** | Lightweight Java app with minimal resource use; no GUI overhead |
| **Societal Impact** | Improves financial literacy for tech-savvy users; accessible on any JDK 17+ system |
| **Ethics** | No data resale; educational-use disclaimer included |

#### 3.3.4 Limitations
1. **No Visualizations** – Lacks graphical charts, limiting user experience compared to web/mobile.  
2. **Manual Input** – Requires keyboard interaction, less intuitive than GUI.  
3. **Persistence Dependency** – SQLite setup needed for full functionality.  
4. **Scalability** – SQLite suits small-scale use; enterprise needs may require migration.  
5. **Internationalisation** – English-only prompts; other locales need localization.

---

<!--## Objectives
List the goals and objectives of the project.

## System Design
### Architecture
Explain the overall architecture of the system.

### Components
Detail the individual components and their roles.

## Implementation
Describe the implementation details, including technologies and tools used.

## Testing
Outline the testing strategies and results.

## Results and Analysis
Present the outcomes and analyze the results.

## Challenges Faced
Discuss the challenges encountered during the project.

## Future Enhancements
Suggest potential improvements or extensions.

## Conclusion
Summarize the project and its impact.

## References
List all references and resources used.-->