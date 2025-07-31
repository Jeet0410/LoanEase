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
        String input = "10000\n0.05\n12\nMonthly\n0\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        cli.start();
        assertTrue(outContent.toString().contains("Period | Principal | Interest | Total Payment | Balance"));
    }

    @Test
    void testStart_InvalidInput() { // Decision table: Invalid principal
        String input = "-100\n0.05\n12\nMonthly\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        cli.start();
        assertTrue(outContent.toString().contains("Error:"));
    }

    @Test
    void testStart_ExportUseCase() { // Use case: Export to CSV/PDF
        String input = "10000\n0.05\n12\nMonthly\n0\ny\ny\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        cli.start();
        assertTrue(outContent.toString().contains("Exported to schedule_"));
    }

    @Test
    void testStart_ScenarioUseCase() { // Use case: Run scenario
        String input = "10000\n0.05\n12\nMonthly\n0\nn\nn\ny\n100\n0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        cli.start();
        assertTrue(outContent.toString().contains("Scenario Schedule:"));
    }

    @Test
    void testStart_StateTransition() { // State transition: Input → Schedule → Export
        String input = "10000\n0.05\n12\nMonthly\n0\ny\ny\nn\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        cli.start();
        String output = outContent.toString();
        assertTrue(output.indexOf("Period | Principal") < output.indexOf("Export schedule to CSV")); // Input to schedule
        assertTrue(output.indexOf("Export schedule to CSV") < output.indexOf("Export schedule to PDF")); // Schedule to export
    }
}