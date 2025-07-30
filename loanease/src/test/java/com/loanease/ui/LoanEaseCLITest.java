package com.loanease.ui;

import com.loanease.model.Loan;
import com.loanease.service.AmortizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

public class LoanEaseCLITest {
    private AmortizationService service;
    private LoanEaseCLI cli;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        service = new AmortizationService();
        cli = new LoanEaseCLI();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testStart_ValidInput() {
        String input = "10000\n0.05\n12\nMonthly\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        cli.start();
        assertTrue(outContent.toString().contains("Period | Principal | Interest | Total Payment | Balance"));
    }
}