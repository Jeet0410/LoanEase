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

The engineering design followed an **iterative, prototype-driven approach**. Each iteration added functionality and testability while revealing shortcomings that informed the next round.

### 3.1 Solution 1 – Web-Based Proof-of-Concept
| Aspect | Description |
|--------|-------------|
| **Implementation** | A basic single-page web application using HTML, CSS, and vanilla JavaScript (`index.html`, `app.js`). Users input loan parameters via a form, and the app calculates and displays a simple amortization table using client-side logic. |
| **Purpose** | Validate the feasibility of a web-based interface and test core financial calculations (e.g., PMT, interest/principal split) in a browser environment before adding complexity. |
| **Testing Focus** | Manual testing of form inputs and table outputs; basic unit tests with Jasmine for numeric helpers; boundary tests for edge cases (0% rate, 1-month term). |
| **Strengths** | ✔ Quick to develop (≈3 days) <br> ✔ Accessible via any browser <br> ✔ Provided initial user feedback on web usability |
| **Weaknesses** | ✖ Limited interactivity—no scenario management or visualizations <br> ✖ No server-side processing or persistence <br> ✖ Basic styling and no export options |
| **Reason Not Selected** | Lacks advanced features like scenario comparison and exports; scaling to meet all requirements would require significant rework. |

### 3.2 Solution 2 – Mobile App Prototype
| Aspect | Description |
|--------|-------------|
| **Implementation** | A lightweight mobile app prototype using Flutter (`main.dart`) targeting Android and iOS. Users input loan details, and the app generates a basic amortization schedule with a simple line chart using the `fl_chart` package. Data is stored locally using SQLite. |
| **Independence from S1** | Independent codebase, exploring a mobile-first approach to broaden accessibility beyond web users. |
| **Testing Focus** | • Unit tests with `test` package for financial calculations <br> • Widget tests for UI components <br> • Integration tests for local storage and chart rendering |
| **Strengths** | ✔ Native mobile experience with offline support <br> ✔ Intuitive touch interface <br> ✔ Quick chart visualization for on-the-go use |
| **Weaknesses** | ✖ Requires app store deployment and user installation <br> ✖ Limited cross-platform testing resources <br> ✖ No multi-scenario comparison or export features |
| **Reason Not Final** | Deployment complexity and lack of web accessibility motivated a return to a web-based solution with broader reach (Section 3.3). |

### 3.3 Final Solution – Web-Based Thin-Client Architecture with CLI Fallback

The final design delivers LoanEase as a **responsive single-page web app** backed by a lightweight REST API, with a CLI fallback for advanced users. Users access the tool through any modern browser or run it locally via the command line without additional installs.

#### 3.3.1 Components
| # | Component | Technology | Purpose | Key Tests |
|---|-----------|------------|---------|-----------|
| 1 | **LoanService API** | Spring Boot (Java 17) | Exposes `/api/schedule` and `/api/scenario` endpoints that accept JSON loan parameters and return amortization schedules. | • JUnit service-level unit tests <br> • Integration tests with MockMvc |
| 2 | **Formula Engine** | Shared Java module (`financial-calculator`) | Houses `FinancialCalculator`, `LoanModel`, rounding utilities. Re-used by both API and CLI fallback. | • Path, data-flow, and boundary tests |
| 3 | **Web Client** | React 18 + Vite | Interactive form, scenario manager, and chart visualizations (Recharts). | • React Testing Library for components <br> • Cypress end-to-end tests |
| 4 | **CLI Fallback** | Java with SLF4J/Apache Commons | Command-line interface for users preferring terminal interaction, reusing the Formula Engine. | • JUnit tests for I/O and scenario logic <br> • Coverage >80% with JaCoCo |
| 5 | **Persistence Layer** | SQLite (Spring Data) | Stores saved loans & scenarios; IndexedDB fallback for offline web use. | • Repository integration tests |
| 6 | **Export Module** | Apache PDFBox & CSV util | Converts schedule JSON into PDF/CSV downloads for both web and CLI. | • Decision-table tests on export formats |

#### 3.3.2 Features
* **Interactive Loan Form** – real-time validation and instant monthly payment preview  
* **Scenario Dashboard** – compare multiple what-if scenarios side-by-side  
* **Dynamic Charts** – line graph (balance) and stacked bar (interest vs. principal)  
* **Export** – one-click CSV and PDF schedule downloads (web) or file output (CLI)  
* **Session Save/Load** – local persistence via IndexedDB (web) or SQLite (CLI); optional cloud sync  
* **Accessibility** – WCAG 2.1 colour contrast & keyboard support (web); CLI usability with clear prompts  
* **CLI Mode** – Terminal-based operation with identical core functionality for power users  

#### 3.3.3 Environmental, Societal, Safety, and Economic Considerations
| Factor | Mitigation / Positive Impact |
|--------|-----------------------------|
| **Economic** | Open-source license avoids vendor lock-in; self-hosting possible; CLI reduces hardware dependency |
| **Regulatory / Security** | Data stays client-side unless user opts for sync; HTTPS; OWASP checks; CLI logs no PII |
| **Reliability** | >90% test coverage (web) and >80% (CLI); CI pipeline; graceful error handling |
| **Sustainability** | Web bundle <150 kB; stateless JSON API → low CPU; CLI lightweight with no GUI overhead |
| **Societal Impact** | Improves financial literacy; inclusive design for assistive tech (web) and terminal users (CLI) |
| **Ethics** | No ads or data resale; clear educational-use disclaimer |

#### 3.3.4 Limitations
1. **Approximation vs. Lender Rules** – Some lenders use proprietary day-count conventions leading to minor discrepancies.  
2. **Offline Complexity** – IndexedDB storage is browser-specific; CLI requires local setup for persistence.  
3. **High-Volume Stress** – SQLite suits small teams; enterprise scale requires PostgreSQL migration.  
4. **Mobile Chart Density** – Very small screens (< 4.7 in) make dual-chart view cramped on web.  
5. **Internationalisation** – English/French supported; other locales need additional formatting patches.

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