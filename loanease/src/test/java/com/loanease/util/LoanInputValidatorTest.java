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
    void testValidate_ValidLoan() {
        Loan loan = new Loan(new BigDecimal("10000"), new BigDecimal("0.05"), 12, "Monthly", BigDecimal.ZERO);
        assertDoesNotThrow(() -> validator.validate(loan));
    }

    @Test
    void testValidate_NegativePrincipal() {
        Loan loan = new Loan(new BigDecimal("-100"), new BigDecimal("0.05"), 12, "Monthly", BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(loan));
    }

    @Test
    void testValidate_ZeroTerm() {
        Loan loan = new Loan(new BigDecimal("10000"), new BigDecimal("0.05"), 0, "Monthly", BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(loan));
    }

    @Test
    void testValidate_InvalidFrequency() {
        Loan loan = new Loan(new BigDecimal("10000"), new BigDecimal("0.05"), 12, "Yearly", BigDecimal.ZERO);
        assertThrows(IllegalArgumentException.class, () -> validator.validate(loan));
    }
}