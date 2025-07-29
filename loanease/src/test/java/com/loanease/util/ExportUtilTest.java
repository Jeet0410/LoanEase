package com.loanease.util;

import com.loanease.model.Loan;
import com.loanease.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ExportUtilTest {
    private ExportUtil exportUtil;
    private List<Payment> schedule;
    private Loan loan;

    @BeforeEach
    void setUp() {
        exportUtil = new ExportUtil();
        schedule = List.of(
            new Payment(1, 1, new BigDecimal("800.00"), new BigDecimal("41.67"), new BigDecimal("9200.00")),
            new Payment(1, 2, new BigDecimal("803.33"), new BigDecimal("38.34"), new BigDecimal("8396.67"))
        );
        loan = new Loan(new BigDecimal("10000.00"), new BigDecimal("0.05"), 12, "Monthly", new BigDecimal("0.00"));
    }

    @Test
    void testExportToCSV_Success() throws IOException {
        String filePath = "test_schedule.csv";
        exportUtil.exportToCSV(schedule, filePath);
        File file = new File(filePath);
        assertTrue(file.exists());
        List<String> lines = Files.readAllLines(file.toPath());
        assertEquals(4, lines.size()); // Header + 2 payments + totals
        assertEquals("Period,Principal,Interest,Total Payment,Balance", lines.get(0));
        assertEquals("1,800.00,41.67,841.67,9200.00", lines.get(1));
        assertEquals("2,803.33,38.34,841.67,8396.67", lines.get(2));
        assertEquals("Totals,1603.33,80.01,1683.34,", lines.get(3));
        file.delete();
    }

    @Test
    void testExportToCSV_EmptySchedule() {
        assertThrows(IllegalArgumentException.class, () -> exportUtil.exportToCSV(Collections.emptyList(), "empty.csv"));
    }

    @Test
    void testExportToCSV_InvalidPath() {
        String invalidPath = "/invalid/path/test.csv";
        assertThrows(IOException.class, () -> exportUtil.exportToCSV(schedule, invalidPath));
    }

    @Test
    void testExportToPDF_Success() throws IOException {
        String filePath = "test_schedule.pdf";
        exportUtil.exportToPDF(loan, schedule, filePath);
        File file = new File(filePath);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        file.delete();
    }

    @Test
    void testExportToPDF_EmptySchedule() {
        assertThrows(IllegalArgumentException.class, () -> exportUtil.exportToPDF(loan, Collections.emptyList(), "empty.pdf"));
    }

    @Test
    void testExportToPDF_NullLoan() {
        assertThrows(IllegalArgumentException.class, () -> exportUtil.exportToPDF(null, schedule, "null_loan.pdf"));
    }

    @Test
    void testExportToPDF_InvalidPath() {
        String invalidPath = "/invalid/path/test.pdf";
        assertThrows(IOException.class, () -> exportUtil.exportToPDF(loan, schedule, invalidPath));
    }

    @Test
    void testExportToPDF_SinglePayment() throws IOException {
        List<Payment> singlePayment = List.of(schedule.get(0));
        String filePath = "single_payment.pdf";
        exportUtil.exportToPDF(loan, singlePayment, filePath);
        File file = new File(filePath);
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
        file.delete();
    }
}