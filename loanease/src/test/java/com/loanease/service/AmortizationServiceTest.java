package com.loanease.service;

import com.loanease.model.Loan;
import com.loanease.model.Payment;
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

    @Test
    void testGenerateSchedule_PrincipalFlow() {
        Loan loan = new Loan(new BigDecimal("5000").setScale(2, RoundingMode.HALF_UP),
                             new BigDecimal("0.06").setScale(4, RoundingMode.HALF_UP),
                             6, "Monthly", BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        long loanId = service.saveLoan(loan);
        List<Payment> schedule = service.generateSchedule(loanId);
        BigDecimal totalPrincipalPaid = schedule.stream()
                .map(Payment::getPrincipalPortion)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(new BigDecimal("5000").setScale(2, RoundingMode.HALF_UP),
                     totalPrincipalPaid.setScale(2, RoundingMode.HALF_UP));
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                     schedule.get(schedule.size() - 1).getRemainingBalance());
    }

    @Test
    void testGenerateSchedule_12Months() {
        Loan loan = new Loan(new BigDecimal("10000").setScale(2, RoundingMode.HALF_UP),
                             new BigDecimal("0.05").setScale(4, RoundingMode.HALF_UP),
                             12, "Monthly", BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        long loanId = service.saveLoan(loan);
        List<Payment> schedule = service.generateSchedule(loanId);
        assertEquals(12, schedule.size());
        assertEquals(new BigDecimal("814.40").setScale(2, RoundingMode.HALF_UP),
                     schedule.get(0).getPrincipalPortion());
        assertEquals(new BigDecimal("41.67").setScale(2, RoundingMode.HALF_UP),
                     schedule.get(0).getInterestPortion());
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                     schedule.get(schedule.size() - 1).getRemainingBalance());
    }
}