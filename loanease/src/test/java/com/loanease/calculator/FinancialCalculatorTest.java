package com.loanease.calculator;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;
import static org.junit.jupiter.api.Assertions.*;

public class FinancialCalculatorTest {
    private final FinancialCalculator calculator = new FinancialCalculator();

    @Test
    void testComputePMT_StandardLoan() {
        BigDecimal principal = new BigDecimal("10000");
        BigDecimal annualRate = new BigDecimal("0.05");
        int termInMonths = 60;
        String frequency = "Monthly";
        BigDecimal expectedPMT = new BigDecimal("188.71"); // Verified with financial formula
        BigDecimal actualPMT = calculator.computePMT(principal, annualRate, termInMonths, frequency);
        assertEquals(expectedPMT.setScale(2, RoundingMode.HALF_UP), 
                     actualPMT.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testComputePMT_ZeroRate() {
        BigDecimal principal = new BigDecimal("10000");
        BigDecimal annualRate = BigDecimal.ZERO;
        int termInMonths = 12;
        String frequency = "Monthly";
        BigDecimal expectedPMT = new BigDecimal("833.33");
        BigDecimal actualPMT = calculator.computePMT(principal, annualRate, termInMonths, frequency);
        assertEquals(expectedPMT.setScale(2, RoundingMode.HALF_UP), 
                     actualPMT.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testRoundCurrency() {
        BigDecimal value = new BigDecimal("123.45678");
        BigDecimal expected = new BigDecimal("123.46");
        assertEquals(expected, calculator.roundCurrency(value));
    }
}