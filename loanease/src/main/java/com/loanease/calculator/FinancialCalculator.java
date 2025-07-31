package com.loanease.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FinancialCalculator {
    public BigDecimal computePMT(BigDecimal principal, BigDecimal annualInterestRate, 
                                int termInMonths, String paymentFrequency) {
        // Input validation
        if (principal == null || principal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Principal must be positive");
        }
        if (annualInterestRate == null || annualInterestRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
        }
        if (termInMonths <= 0) {
            throw new IllegalArgumentException("Term must be positive");
        }
        if (!paymentFrequency.equalsIgnoreCase("Monthly")) {
            throw new IllegalArgumentException("Only Monthly frequency is supported");
        }

        BigDecimal ratePerPeriod = annualInterestRate.divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
        int periods = termInMonths;

        if (ratePerPeriod.compareTo(BigDecimal.ZERO) == 0) {
            return principal.divide(new BigDecimal(periods), 2, RoundingMode.HALF_UP);
        }

        // PMT = P * [r(1+r)^n] / [(1+r)^n - 1]
        BigDecimal onePlusRate = BigDecimal.ONE.add(ratePerPeriod);
        BigDecimal onePlusRatePowN = onePlusRate.pow(periods);
        BigDecimal numerator = ratePerPeriod.multiply(onePlusRatePowN);
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