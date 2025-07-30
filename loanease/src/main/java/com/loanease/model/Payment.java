package com.loanease.model;

import java.math.BigDecimal;

public class Payment {
    private long loanId; // Foreign key to Loan
    private int period;
    private BigDecimal principalPortion;
    private BigDecimal interestPortion;
    private BigDecimal remainingBalance;

    public Payment(long loanId, int period, BigDecimal principalPortion, BigDecimal interestPortion, 
                   BigDecimal remainingBalance) {
        this.loanId = loanId;
        this.period = period;
        this.principalPortion = principalPortion;
        this.interestPortion = interestPortion;
        this.remainingBalance = remainingBalance;
    }

    // Getters
    public long getLoanId() { return loanId; }
    public int getPeriod() { return period; }
    public BigDecimal getPrincipalPortion() { return principalPortion; }
    public BigDecimal getInterestPortion() { return interestPortion; }
    public BigDecimal getRemainingBalance() { return remainingBalance; }
}