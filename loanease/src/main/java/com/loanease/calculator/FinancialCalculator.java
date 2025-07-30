package com.loanease.calculator;

import org.apache.commons.math3.util.Precision;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class FinancialCalculator {
    public BigDecimal computePMT(BigDecimal principal, BigDecimal annualInterestRate, 
                                int termInMonths, String paymentFrequency) {
        BigDecimal monthlyRate = annualInterestRate.divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
        int periods = termInMonths; // Adjust for frequency if needed (e.g., bi-weekly)
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(new BigDecimal(periods), 2, RoundingMode.HALF_UP);
        }
        // PMT = P * [r(1+r)^n] / [(1+r)^n - 1]
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);
        BigDecimal onePlusRatePowN = onePlusRate.pow(periods);
        BigDecimal numerator = monthlyRate.multiply(onePlusRatePowN);
        BigDecimal denominator = onePlusRatePowN.subtract(BigDecimal.ONE);
        return principal.multiply(numerator).divide(denominator, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal roundCurrency(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal interestPortion(BigDecimal remainingBalance, BigDecimal monthlyRate) {
        return roundCurrency(remainingBalance.multiply(monthlyRate));
    }

    public BigDecimal principalPortion(BigDecimal payment, BigDecimal interestPortion) {
        return roundCurrency(payment.subtract(interestPortion));
    }
}