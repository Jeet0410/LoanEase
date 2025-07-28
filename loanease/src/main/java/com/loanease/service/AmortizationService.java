package com.loanease.service;

import com.loanease.model.*;
import com.loanease.calculator.FinancialCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AmortizationService {
    private static final Logger logger = LoggerFactory.getLogger(AmortizationService.class);
    private final FinancialCalculator calculator = new FinancialCalculator();
    private final DatabaseService dbService = new DatabaseService();

    public long saveLoan(Loan loan) {
        logger.info("Saving loan: principal={}, rate={}, term={}", 
                    loan.getPrincipal(), loan.getAnnualInterestRate(), loan.getTermInMonths());
        return dbService.saveLoan(loan);
    }

    public List<Payment> generateSchedule(long loanId) {
        logger.info("Generating schedule for loanId={}", loanId);
        Loan loan = getLoanById(loanId);
        if (loan == null) {
            logger.error("Loan not found for loanId={}", loanId);
            throw new IllegalArgumentException("Loan not found");
        }
        List<Payment> schedule = new ArrayList<>();
        BigDecimal principal = loan.getPrincipal();
        BigDecimal monthlyRate = loan.getAnnualInterestRate().divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
        int periods = loan.getTermInMonths();
        BigDecimal payment = calculator.computePMT(principal, loan.getAnnualInterestRate(), periods, loan.getPaymentFrequency());
        payment = payment.add(loan.getExtraPayment());

        BigDecimal remainingBalance = principal;
        for (int period = 1; period <= periods && remainingBalance.compareTo(BigDecimal.ZERO) > 0; period++) {
            BigDecimal interest = calculator.interestPortion(remainingBalance, monthlyRate);
            BigDecimal principalPortion = calculator.principalPortion(payment, interest);
            if (period == periods || principalPortion.compareTo(remainingBalance) > 0) {
                principalPortion = remainingBalance;
                payment = principalPortion.add(interest);
            }
            remainingBalance = remainingBalance.subtract(principalPortion);
            Payment p = new Payment(loanId, period, 
                                   principalPortion.setScale(2, RoundingMode.HALF_UP),
                                   interest.setScale(2, RoundingMode.HALF_UP),
                                   remainingBalance.setScale(2, RoundingMode.HALF_UP));
            schedule.add(p);
        }
        logger.info("Schedule generated with {} payments", schedule.size());
        dbService.saveSchedule(loanId, schedule);
        return schedule;
    }

    public LoanSchedulePair runScenario(long loanId, BigDecimal newExtraPayment, BigDecimal newRate) {
        logger.info("Running scenario for loanId={}, extraPayment={}, newRate={}", 
                    loanId, newExtraPayment, newRate);
        Loan loan = getLoanById(loanId);
        if (loan == null) {
            throw new IllegalArgumentException("Loan not found");
        }
        Loan scenarioLoan = new Loan(loan.getPrincipal(), 
                                    newRate != null ? newRate : loan.getAnnualInterestRate(),
                                    loan.getTermInMonths(), 
                                    loan.getPaymentFrequency(), 
                                    newExtraPayment != null ? newExtraPayment : loan.getExtraPayment());
        long scenarioLoanId = dbService.saveLoan(scenarioLoan);
        List<Payment> scenarioSchedule = generateSchedule(scenarioLoanId);
        return new LoanSchedulePair(scenarioLoan, scenarioSchedule);
    }

    private Loan getLoanById(long loanId) {
        String sql = "SELECT principal, annual_interest_rate, term_in_months, payment_frequency, extra_payment " +
                     "FROM loans WHERE id = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:loanease.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, loanId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Loan(
                    rs.getBigDecimal("principal"),
                    rs.getBigDecimal("annual_interest_rate"),
                    rs.getInt("term_in_months"),
                    rs.getString("payment_frequency"),
                    rs.getBigDecimal("extra_payment")
                );
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve loan: {}", e.getMessage());
        }
        return null;
    }

    // Helper class to return both Loan and Schedule
    public static class LoanSchedulePair {
        private final Loan loan;
        private final List<Payment> schedule;

        public LoanSchedulePair(Loan loan, List<Payment> schedule) {
            this.loan = loan;
            this.schedule = schedule;
        }

        public Loan getLoan() {
            return loan;
        }

        public List<Payment> getSchedule() {
            return schedule;
        }
    }
}