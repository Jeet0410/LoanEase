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
        BigDecimal expectedPMT = new BigDecimal("188.71");
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
    void testComputePMT_WeeklyFrequency() {
        BigDecimal principal = new BigDecimal("10000");
        BigDecimal annualRate = new BigDecimal("0.05");
        int termInMonths = 12;
        String frequency = "Weekly";
        assertThrows(IllegalArgumentException.class, () -> 
            calculator.computePMT(principal, annualRate, termInMonths, frequency));
    }

    @Test
    void testComputePMT_NegativePrincipal() {
        assertThrows(IllegalArgumentException.class, () -> 
            calculator.computePMT(new BigDecimal("-100"), new BigDecimal("0.05"), 12, "Monthly"));
    }

    @Test
    void testComputePMT_MaxPrincipal() {
        BigDecimal principal = new BigDecimal("1000000");
        BigDecimal annualRate = new BigDecimal("0.05");
        int termInMonths = 12;
        String frequency = "Monthly";
        BigDecimal expectedPMT = new BigDecimal("85607.48");
        BigDecimal actualPMT = calculator.computePMT(principal, annualRate, termInMonths, frequency);
        assertEquals(expectedPMT.setScale(2, RoundingMode.HALF_UP), 
                     actualPMT.setScale(2, RoundingMode.HALF_UP));
    }

    @Test
    void testComputePMT_DataFlow() {
        BigDecimal principal = new BigDecimal("5000");
        BigDecimal annualRate = new BigDecimal("0.06");
        int termInMonths = 6;
        String frequency = "Monthly";
        BigDecimal pmt = calculator.computePMT(principal, annualRate, termInMonths, frequency);
        BigDecimal interest = calculator.interestPortion(principal, annualRate.divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP));
        BigDecimal principalPortion = calculator.principalPortion(pmt, interest);
        assertEquals(new BigDecimal("847.98").setScale(2, RoundingMode.HALF_UP), pmt);
        assertEquals(new BigDecimal("25.00").setScale(2, RoundingMode.HALF_UP), interest);
        assertEquals(new BigDecimal("822.98").setScale(2, RoundingMode.HALF_UP), principalPortion);
    }

    @Test
    void testRoundCurrency() {
        BigDecimal value = new BigDecimal("123.45678");
        BigDecimal expected = new BigDecimal("123.46");
        assertEquals(expected, calculator.roundCurrency(value));
    }
}