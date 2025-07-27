package com.loanease.service;

import com.loanease.model.Loan;
import com.loanease.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseServiceTest {
    private DatabaseService dbService;

    @BeforeEach
    void setUp() {
        dbService = new DatabaseService();
    }

    @Test
    void testSaveAndRetrieveLoanAndSchedule() {
        Loan loan = new Loan(new BigDecimal("10000").setScale(2, RoundingMode.HALF_UP), 
                             new BigDecimal("0.05").setScale(4, RoundingMode.HALF_UP), 
                             12, "Monthly", 
                             new BigDecimal("0").setScale(2, RoundingMode.HALF_UP));
        long loanId = dbService.saveLoan(loan);
        assertTrue(loanId > 0);

        List<Payment> schedule = List.of(
            new Payment(loanId, 1, 
                        new BigDecimal("800.00").setScale(2, RoundingMode.HALF_UP), 
                        new BigDecimal("41.67").setScale(2, RoundingMode.HALF_UP), 
                        new BigDecimal("9200.00").setScale(2, RoundingMode.HALF_UP))
        );
        dbService.saveSchedule(loanId, schedule);

        List<Payment> retrieved = dbService.getSchedule(loanId);
        assertEquals(1, retrieved.size());
        assertEquals(schedule.get(0).getPrincipalPortion().setScale(2, RoundingMode.HALF_UP), 
                     retrieved.get(0).getPrincipalPortion().setScale(2, RoundingMode.HALF_UP));
    }
}