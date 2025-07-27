package com.loanease.util;

import com.loanease.model.Loan;
import java.math.BigDecimal;

public class LoanInputValidator {
    public void validate(Loan loan) {
        if (loan.getPrincipal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Principal must be positive");
        }
        if (loan.getAnnualInterestRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Interest rate cannot be negative");
        }
        if (loan.getTermInMonths() <= 0) {
            throw new IllegalArgumentException("Term must be positive");
        }
        if (!loan.getPaymentFrequency().equalsIgnoreCase("Monthly")) {
            throw new IllegalArgumentException("Only Monthly frequency is supported");
        }
        if (loan.getExtraPayment().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Extra payment cannot be negative");
        }
    }
}