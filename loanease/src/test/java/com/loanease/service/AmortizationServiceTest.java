package com.loanease.service;

import com.loanease.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AmortizationServiceTest {
    private AmortizationService service;

    @BeforeEach
    void setUp() {
        service = new AmortizationService();
    }

    // Path Testing: Test all paths in generateSchedule
    @Test
    void testGenerateSchedule_StandardLoan() {
        Loan loan = new Loan(new BigDecimal("10000"), new BigDecimal("0.05"), 12, "Monthly", BigDecimal.ZERO);
        long loanId = service.saveLoan(loan);
        List<Payment> schedule = service.generateSchedule(loanId);
        assertEquals(12, schedule.size());
        Payment first = schedule.get(0);
        assertEquals(1, first.getPeriod());
        assertTrue(first.getRemainingBalance().compareTo(new BigDecimal("10000")) < 0);
    }

    // Data Flow Testing: Test principal variable flow
    @Test
    void testGenerateSchedule_PrincipalFlow() {
        Loan loan = new Loan(new BigDecimal("5000"), new BigDecimal("0.06"), 6, "Monthly", BigDecimal.ZERO);
        long loanId = service.saveLoan(loan);
        List<Payment> schedule = service.generateSchedule(loanId);
        BigDecimal totalPrincipalPaid = schedule.stream()
                .map(Payment::getPrincipalPortion)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(new BigDecimal("5000").setScale(2, RoundingMode.HALF_UP), 
                     totalPrincipalPaid.setScale(2, RoundingMode.HALF_UP));
    }
}