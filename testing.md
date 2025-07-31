# TESTING.md

## 1. Introduction
This document describes the comprehensive test plan for the LoanEase project, demonstrating fulfillment of all course requirements. We leveraged **JUnit 5** to implement and execute unit, integration, and validation tests covering all specified techniques.

## 2. Course Testing Requirements
Your professor required that the application be tested using the following techniques:

1. **Function-Level Tests**
   - **Path Testing**
   - **Data Flow Testing**n
2. **Integration Testing**
   - Test a subset of units together

3. **Validation Tests**
   - **Boundary Value Testing**
   - **Equivalence Class Testing**
   - **Decision Table Testing**
   - **State Transition Testing**
   - **Use Case Testing**

Each of these is covered in sections 3–9 below.

## 3. Function-Level Tests

### 3.1 Path Testing
**Objective:** Cover all code paths in critical functions.

**Target Function:** `FinancialCalculator.computePMT(...)` and error paths in `ExportUtil.exportToCSV(...)`.

| Test Method                                             | Description                                           |
|---------------------------------------------------------|-------------------------------------------------------|
| `testComputePMT_StandardLoan()`                         | Normal amortization path (5-year, 5% rate)            |
| `testComputePMT_ZeroRate()`                             | Zero-interest special branch (`ratePerPeriod == 0`)   |
| `testComputePMT_WeeklyFrequency()`                      | Invalid frequency → throws `IllegalArgumentException` |
| `testExportToCSV_EmptySchedule()`                       | Empty schedule → throws `IllegalArgumentException`    |

### 3.2 Data Flow Testing
**Objective:** Validate internal data transformations (principal → interest → payment portions).

**Target Function:** Combination of `computePMT`, `interestPortion`, and `principalPortion`.

| Test Method                         | Verifies                                                    |
|-------------------------------------|-------------------------------------------------------------|
| `testComputePMT_DataFlow()`         | PMT, interest, and principalPortion for $5 000 @ 6% / 6mo.   |
| `testRoundCurrency()`               | Rounding logic for arbitrary decimal values                |

## 4. Integration Testing
**Objective:** Test interactions among modules: calculator, service, database, and export utilities.

**Selected Subset:** `AmortizationService`, `DatabaseService`, and `ExportUtil`.

| Test Method                                              | Modules Covered                                         |
|----------------------------------------------------------|---------------------------------------------------------|
| `AmortizationServiceTest.testGenerateSchedule_PrincipalFlow()` | `AmortizationService` → `DatabaseService`                |
| `AmortizationServiceTest.testGenerateSchedule_12Months()`     | Schedule generation logic with saved loan data          |
| `DatabaseServiceTest.testSaveAndRetrieveLoanAndSchedule()`    | `DatabaseService` persistence and retrieval             |
| `ExportUtilTest.testIntegrationWithAmortizationService()`     | `AmortizationService` + `ExportUtil` CSV export         |

## 5. Boundary Value Testing
**Objective:** Verify behavior at input extremes.

| Test Method                                      | Boundary Condition                    |
|--------------------------------------------------|---------------------------------------|
| `testComputePMT_NegativePrincipal()`             | Principal < 0 (invalid)               |
| `testComputePMT_MaxPrincipal()`                  | Principal = $1 000 000                |
| `LoanInputValidatorTest.testValidate_ZeroTerm()` | Term = 0 (invalid)                    |
| `testExportToCSV_EmptySchedule()`                | Zero-length schedule (invalid)        |

## 6. Equivalence Class Testing
**Objective:** Group inputs into valid/invalid classes and test representatives.

| Equivalence Class       | Valid Representative        | Invalid Representative          | Test Method                            |
|-------------------------|-----------------------------|---------------------------------|----------------------------------------|
| Principal               | 1 000                       | -1 000                          | `testValidate_ValidInput`, `testValidate_NegativePrincipal` |
| Annual Rate (%)         | 5%                          | -5%                             | `testValidate_NegativeRate`            |
| Term (months)           | 12                          | 0                               | `testValidate_ZeroTerm`                |
| Frequency               | "Monthly"                 | "Weekly"                      | `testValidate_InvalidFrequency`        |
| Extra Payment           | 100                         | -100                            | `testValidate_NegativeExtraPayment`    |

## 7. Decision Table Testing
**Objective:** Verify combinations of input conditions using a decision table.

| # | Principal>0 | Rate≥0 | Term>0 | Freq=Monthly | Extra≥0 | Expected Outcome | Test Method                        |
|---|-------------|--------|--------|--------------|---------|------------------|------------------------------------|
| 1 | Y           | Y      | Y      | Y            | Y       | Pass             | `testValidate_ValidInput`          |
| 2 | N           | Y      | Y      | Y            | Y       | Fail             | `testValidate_NegativePrincipal`   |
| 3 | Y           | N      | Y      | Y            | Y       | Fail             | `testValidate_NegativeRate`        |
| 4 | Y           | Y      | N      | Y            | Y       | Fail             | `testValidate_ZeroTerm`            |
| 5 | Y           | Y      | Y      | N            | Y       | Fail             | `testValidate_InvalidFrequency`    |
| 6 | Y           | Y      | Y      | Y            | N       | Fail             | `testValidate_NegativeExtraPayment`|
|   | **CSV Variant** |         |        |              |         |                  | `testExportToCSV_DecisionTable`    |

## 8. State Transition Testing
**Objective:** Confirm CLI state changes (input → schedule display → export prompts).

| Test Method                                 | Scenario                                                                 |
|---------------------------------------------|--------------------------------------------------------------------------|
| `LoanEaseCLITest.testStart_StateTransition` | Valid input leads to schedule print, then export prompts in correct order |

## 9. Use Case Testing
**Objective:** End-to-end scenarios from user perspective.

| Use Case # | Description                                          | Test Method                       |
|------------|------------------------------------------------------|-----------------------------------|
| 1          | Display amortization schedule for valid loan input   | `testStart_ValidInput`            |
| 2          | Export schedule to CSV and PDF                      | `testStart_ExportUseCase`         |
| 3          | Run “extra payment” scenario                         | `testStart_ScenarioUseCase`       |

## 10. Tools & Coverage
- **Framework:** JUnit 5, Maven, JDK
- **Code Coverage:** > 80 % overall (measured via Java Code Runner JUnit Extension Coverage)

### Test Coverage Snapshot

![Test Coverage](/loanease/src/main/resources/Screenshot%202025-07-31%20at%2012.47.18 PM.png)

---

## 11. Execution Instructions
### Option 1: Use the Testing Sidebar

1. Click the **Testing** icon (🧪) in the sidebar.
2. You’ll see your test class and test methods listed.
3. Right Click the **loanease** icon and choose 'Run Test with Coverage'.

### Option 2: Right-Click in Code

1. Inside any test method, right-click and choose:
   - **Run Test with coverage** or **Debug Test**

### Option 3: Command line  
1. If you want to use the command line then: 
    ```bash
    mvn test
    ```

   
VSCode will show test results in the **Test Output Panel**.



All outputs are automatically compared to expected results; failures will report mismatches.
