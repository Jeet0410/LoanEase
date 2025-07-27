package com.loanease.util;

import com.loanease.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ExportUtilTest {
    private ExportUtil exportUtil;
    private List<Payment> schedule;

    @BeforeEach
    void setUp() {
        exportUtil = new ExportUtil();
        schedule = List.of(new Payment(1, 1, new BigDecimal("800.00"), 
                                     new BigDecimal("41.67"), new BigDecimal("9200.00")));
    }

    @Test
    void testExportToCSV() throws Exception {
        String filePath = "test_schedule.csv";
        exportUtil.exportToCSV(schedule, filePath);
        File file = new File(filePath);
        assertTrue(file.exists());
        String content = Files.readString(file.toPath());
        assertTrue(content.contains("Period,Principal,Interest,Balance"));
        assertTrue(content.contains("1,800.00,41.67,9200.00"));
        file.delete();
    }

    @Test
    void testExportToPDF() throws Exception {
        String filePath = "test_schedule.pdf";
        exportUtil.exportToPDF(schedule, filePath);
        File file = new File(filePath);
        assertTrue(file.exists());
        file.delete();
    }
}