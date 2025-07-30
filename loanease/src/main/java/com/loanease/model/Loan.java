package com.loanease.model;

import java.math.BigDecimal;

public class Loan {
    private long id; // For database storage
    private BigDecimal principal;
    private BigDecimal annualInterestRate;
    private int termInMonths;
    private String paymentFrequency;
    private BigDecimal extraPayment;

    public Loan(BigDecimal principal, BigDecimal annualInterestRate, int termInMonths, 
                String paymentFrequency, BigDecimal extraPayment) {
        this.principal = principal;
        this.annualInterestRate = annualInterestRate;
        this.termInMonths = termInMonths;
        this.paymentFrequency = paymentFrequency;
        this.extraPayment = extraPayment != null ? extraPayment : BigDecimal.ZERO;
    }

    // Getters and setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public BigDecimal getPrincipal() { return principal; }
    public BigDecimal getAnnualInterestRate() { return annualInterestRate; }
    public int getTermInMonths() { return termInMonths; }
    public String getPaymentFrequency() { return paymentFrequency; }
    public BigDecimal getExtraPayment() { return extraPayment; }
}