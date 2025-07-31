package com.loanease.util;

import com.loanease.model.Loan;
import com.loanease.model.Payment;
import com.loanease.service.AmortizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ExportUtilTest {
    private ExportUtil exportUtil;
    private AmortizationService amortizationService;

    @BeforeEach
    void setUp() {
        exportUtil = new ExportUtil();
        amortizationService = new AmortizationService();
    }

    @Test
    void testExportToCSV_Success() throws IOException, SQLException {
        Loan loan = new Loan(new BigDecimal("10000").setScale(2, RoundingMode.HALF_UP),
                             new BigDecimal("0.05").setScale(4, RoundingMode.HALF_UP),
                             12, "Monthly", BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        long loanId = amortizationService.saveLoan(loan);
        List<Payment> schedule = amortizationService.generateSchedule(loanId);

        String filePath = "test_schedule.csv";
        exportUtil.exportToCSV(schedule, filePath);

        File file = new File(filePath);
        assertTrue(file.exists());
        List<String> lines = Files.readAllLines(file.toPath());
        assertEquals(schedule.size() + 2, lines.size()); // Header + payments + totals row
        String header = lines.get(0);
        assertEquals("Period,Principal,Interest,Total Payment,Balance", header);
        String firstPayment = lines.get(1);
        assertTrue(firstPayment.contains("814.40")); // Principal portion
        assertTrue(firstPayment.contains("41.67")); // Interest portion
        assertTrue(firstPayment.contains("856.07")); // Total Payment
        String totalsRow = lines.get(lines.size() - 1);
        assertTrue(totalsRow.startsWith("Totals"));
        assertTrue(totalsRow.contains("10000.00")); // Total principal
        file.delete(); // Clean up
    }

    @Test
    void testExportToCSV_EmptySchedule() {
        List<Payment> emptySchedule = Arrays.asList();
        assertThrows(IllegalArgumentException.class, () -> exportUtil.exportToCSV(emptySchedule, "empty.csv"));
    }

    @Test
    void testExportToPDF_Success() throws IOException, SQLException {
        Loan loan = new Loan(new BigDecimal("10000").setScale(2, RoundingMode.HALF_UP),
                             new BigDecimal("0.05").setScale(4, RoundingMode.HALF_UP),
                             12, "Monthly", BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        long loanId = amortizationService.saveLoan(loan);
        List<Payment> schedule = amortizationService.generateSchedule(loanId);

        String filePath = "test_schedule.pdf";
        exportUtil.exportToPDF(loan, schedule, filePath);

        File file = new File(filePath);
        assertTrue(file.exists());
        // Basic PDF validation (file size > 0 indicates content)
        assertTrue(file.length() > 0);
        file.delete(); // Clean up
    }

    @Test
    void testExportToPDF_NullLoan() {
        List<Payment> schedule = Arrays.asList(new Payment(1L, 1, new BigDecimal("100"), new BigDecimal("10"), new BigDecimal("900")));
        assertThrows(IllegalArgumentException.class, () -> exportUtil.exportToPDF(null, schedule, "null_loan.pdf"));
    }

    @Test
    void testExportToCSV_DecisionTable() throws IOException, SQLException {
        // Valid case
        Loan loan = new Loan(new BigDecimal("5000"), new BigDecimal("0.06"), 6, "Monthly", BigDecimal.ZERO);
        long loanId = amortizationService.saveLoan(loan);
        List<Payment> schedule = amortizationService.generateSchedule(loanId);
        exportUtil.exportToCSV(schedule, "decision_table.csv");
        File file = new File("decision_table.csv");
        assertTrue(file.exists());
        file.delete();

        // Empty schedule
        assertThrows(IllegalArgumentException.class, () -> exportUtil.exportToCSV(Arrays.asList(), "empty.csv"));

        // Invalid file path
        List<Payment> validSchedule = Arrays.asList(new Payment(1L, 1, new BigDecimal("100"), new BigDecimal("10"), new BigDecimal("900")));
        assertThrows(IOException.class, () -> exportUtil.exportToCSV(validSchedule, "/invalid/path/schedule.csv"));
    }

    @Test
    void testIntegrationWithAmortizationService() throws SQLException, IOException {
        Loan loan = new Loan(new BigDecimal("5000"), new BigDecimal("0.06"), 6, "Monthly", BigDecimal.ZERO);
        long loanId = amortizationService.saveLoan(loan);
        List<Payment> schedule = amortizationService.generateSchedule(loanId);
        String filePath = "integration_schedule.csv";
        exportUtil.exportToCSV(schedule, filePath);
        File file = new File(filePath);
        assertTrue(file.exists());
        List<String> lines = Files.readAllLines(file.toPath());
        assertEquals(schedule.size() + 2, lines.size()); // Header + payments + totals row
        file.delete();
    }
}