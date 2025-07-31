package com.loanease.util;

import com.loanease.model.Loan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class LoanInputValidatorTest {
    private LoanInputValidator validator;

    @BeforeEach
    void setUp() {
        validator = new LoanInputValidator();
    }

    @Test
    void testValidate_ValidInput() {
        Loan loan = new Loan(new BigDecimal("1000"), new BigDecimal("0.05"), 12, "Monthly", BigDecimal.ZERO);
        assertDoesNotThrow(() -> validator.validate(loan));
    }

    @Test
    void testValidate_NegativePrincipal() {
        Loan loan = new Loan(new BigDecimal("-1000"), new BigDecimal("0.05"), 12, "Monthly", BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(loan));
    }

    @Test
    void testValidate_NegativeRate() {
        Loan loan = new Loan(new BigDecimal("1000"), new BigDecimal("-0.05"), 12, "Monthly", BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(loan));
    }

    @Test
    void testValidate_ZeroTerm() {
        Loan loan = new Loan(new BigDecimal("1000"), new BigDecimal("0.05"), 0, "Monthly", BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(loan));
    }

    @Test
    void testValidate_InvalidFrequency() {
        Loan loan = new Loan(new BigDecimal("1000"), new BigDecimal("0.05"), 12, "Weekly", BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(loan));
    }

    @Test
    void testValidate_NegativeExtraPayment() {
        Loan loan = new Loan(new BigDecimal("1000"), new BigDecimal("0.05"), 12, "Monthly", new BigDecimal("-100"));
        assertThrows(IllegalArgumentException.class, () -> validator.validate(loan));
    }

    @Test
    void testValidate_DecisionTable() {
        // Valid case
        Loan validLoan = new Loan(new BigDecimal("1000"), new BigDecimal("0.05"), 12, "Monthly", BigDecimal.ZERO);
        assertDoesNotThrow(() -> validator.validate(validLoan));

        // Invalid cases
        Loan negativePrincipal = new Loan(new BigDecimal("-1000"), new BigDecimal("0.05"), 12, "Monthly", BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(negativePrincipal));

        Loan negativeRate = new Loan(new BigDecimal("1000"), new BigDecimal("-0.05"), 12, "Monthly", BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(negativeRate));

        Loan invalidFrequency = new Loan(new BigDecimal("1000"), new BigDecimal("0.05"), 12, "Yearly", BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(invalidFrequency));
    }
}