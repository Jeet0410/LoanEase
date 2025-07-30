package com.loanease.service;

import com.loanease.model.Loan;
import com.loanease.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);
    private static final String DB_URL = "jdbc:sqlite:loanease.db";

    public DatabaseService() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS loans (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    principal DECIMAL(15,2),
                    annual_interest_rate DECIMAL(5,4),
                    term_in_months INTEGER,
                    payment_frequency TEXT,
                    extra_payment DECIMAL(15,2)
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS payments (
                    loan_id INTEGER,
                    period INTEGER,
                    principal_portion DECIMAL(15,2),
                    interest_portion DECIMAL(15,2),
                    remaining_balance DECIMAL(15,2),
                    PRIMARY KEY (loan_id, period),
                    FOREIGN KEY (loan_id) REFERENCES loans(id)
                )
            """);
            logger.info("Database initialized successfully");
        } catch (SQLException e) {
            logger.error("Failed to initialize database: {}", e.getMessage());
            throw new RuntimeException("Failed to initialize database: " + e.getMessage());
        }
    }

    public long saveLoan(Loan loan) {
        String sql = "INSERT INTO loans (principal, annual_interest_rate, term_in_months, payment_frequency, extra_payment) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setBigDecimal(1, loan.getPrincipal().setScale(2, RoundingMode.HALF_UP));
            pstmt.setBigDecimal(2, loan.getAnnualInterestRate().setScale(4, RoundingMode.HALF_UP));
            pstmt.setInt(3, loan.getTermInMonths());
            pstmt.setString(4, loan.getPaymentFrequency());
            pstmt.setBigDecimal(5, loan.getExtraPayment().setScale(2, RoundingMode.HALF_UP));
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                long loanId = rs.getLong(1);
                logger.info("Loan saved with ID: {}", loanId);
                return loanId;
            }
            throw new SQLException("Failed to retrieve loan ID");
        } catch (SQLException e) {
            logger.error("Failed to save loan: {}", e.getMessage());
            throw new RuntimeException("Failed to save loan: " + e.getMessage());
        }
    }

    public void saveSchedule(long loanId, List<Payment> schedule) {
        String sql = "INSERT INTO payments (loan_id, period, principal_portion, interest_portion, remaining_balance) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Payment payment : schedule) {
                pstmt.setLong(1, loanId);
                pstmt.setInt(2, payment.getPeriod());
                pstmt.setBigDecimal(3, payment.getPrincipalPortion().setScale(2, RoundingMode.HALF_UP));
                pstmt.setBigDecimal(4, payment.getInterestPortion().setScale(2, RoundingMode.HALF_UP));
                pstmt.setBigDecimal(5, payment.getRemainingBalance().setScale(2, RoundingMode.HALF_UP));
                pstmt.executeUpdate();
            }
            logger.info("Schedule saved for loanId={}", loanId);
        } catch (SQLException e) {
            logger.error("Failed to save schedule: {}", e.getMessage());
            throw new RuntimeException("Failed to save schedule: " + e.getMessage());
        }
    }

    public List<Payment> getSchedule(long loanId) {
        List<Payment> schedule = new ArrayList<>();
        String sql = "SELECT period, principal_portion, interest_portion, remaining_balance " +
                     "FROM payments WHERE loan_id = ? ORDER BY period";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, loanId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                schedule.add(new Payment(
                    loanId,
                    rs.getInt("period"),
                    rs.getBigDecimal("principal_portion").setScale(2, RoundingMode.HALF_UP),
                    rs.getBigDecimal("interest_portion").setScale(2, RoundingMode.HALF_UP),
                    rs.getBigDecimal("remaining_balance").setScale(2, RoundingMode.HALF_UP)
                ));
            }
            logger.info("Retrieved schedule with {} payments for loanId={}", schedule.size(), loanId);
        } catch (SQLException e) {
            logger.error("Failed to retrieve schedule: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve schedule: " + e.getMessage());
        }
        return schedule;
    }
}