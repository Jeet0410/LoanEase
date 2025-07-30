package com.loanease.util;

import com.loanease.model.Loan;
import com.loanease.model.Payment;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ExportUtil {
    private static final Logger logger = LoggerFactory.getLogger(ExportUtil.class);

    public void exportToCSV(List<Payment> schedule, String filePath) throws IOException {
        if (schedule == null || schedule.isEmpty()) {
            logger.error("Cannot export to CSV: Schedule is null or empty");
            throw new IllegalArgumentException("Schedule is null or empty");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Period,Principal,Interest,Total Payment,Balance\n");
            BigDecimal totalPrincipal = BigDecimal.ZERO;
            BigDecimal totalInterest = BigDecimal.ZERO;
            BigDecimal totalPayment = BigDecimal.ZERO;
            for (Payment payment : schedule) {
                BigDecimal principal = payment.getPrincipalPortion().setScale(2, RoundingMode.HALF_UP);
                BigDecimal interest = payment.getInterestPortion().setScale(2, RoundingMode.HALF_UP);
                BigDecimal paymentAmount = principal.add(interest).setScale(2, RoundingMode.HALF_UP);
                totalPrincipal = totalPrincipal.add(principal);
                totalInterest = totalInterest.add(interest);
                totalPayment = totalPayment.add(paymentAmount);
                writer.write(String.format("%d,%.2f,%.2f,%.2f,%.2f\n",
                        payment.getPeriod(),
                        principal,
                        interest,
                        paymentAmount,
                        payment.getRemainingBalance().setScale(2, RoundingMode.HALF_UP)));
            }
            // Totals row
            writer.write(String.format("Totals,%.2f,%.2f,%.2f,\n",
                    totalPrincipal.setScale(2, RoundingMode.HALF_UP),
                    totalInterest.setScale(2, RoundingMode.HALF_UP),
                    totalPayment.setScale(2, RoundingMode.HALF_UP)));
            logger.info("Exported schedule to CSV: {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to export CSV to {}: {}", filePath, e.getMessage());
            throw new IOException("Failed to export CSV: " + e.getMessage());
        }
    }

    public void exportToPDF(Loan loan, List<Payment> schedule, String filePath) throws IOException {
        if (schedule == null || schedule.isEmpty() || loan == null) {
            logger.error("Cannot export to PDF: Schedule or loan is null or empty");
            throw new IllegalArgumentException("Schedule or loan is null or empty");
        }
        try (PdfWriter writer = new PdfWriter(filePath);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {
            // Title
            document.add(new Paragraph("LoanEase Amortization Schedule")
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Loan Details
            document.add(new Paragraph("Loan Details")
                    .setFontSize(14)
                    .setBold()
                    .setMarginBottom(10));
            document.add(new Paragraph(String.format("Principal: $%,.2f", 
                    loan.getPrincipal().setScale(2, RoundingMode.HALF_UP)))
                    .setFontSize(12));
            document.add(new Paragraph(String.format("Annual Interest Rate: %.2f%%", 
                    loan.getAnnualInterestRate().multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP)))
                    .setFontSize(12));
            document.add(new Paragraph(String.format("Term: %d months", loan.getTermInMonths()))
                    .setFontSize(12));
            document.add(new Paragraph(String.format("Payment Frequency: %s", loan.getPaymentFrequency()))
                    .setFontSize(12));
            document.add(new Paragraph(String.format("Extra Payment per Period: $%,.2f", 
                    loan.getExtraPayment().setScale(2, RoundingMode.HALF_UP)))
                    .setFontSize(12)
                    .setMarginBottom(20));

            // Schedule Table
            Table table = new Table(UnitValue.createPercentArray(new float[]{20, 20, 20, 20, 20}))
                    .setWidth(UnitValue.createPercentValue(100));
            // Headers
            table.addHeaderCell(new Cell().add(new Paragraph("Period").setBold()).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Principal").setBold()).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Interest").setBold()).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Total Payment").setBold()).setTextAlignment(TextAlignment.CENTER));
            table.addHeaderCell(new Cell().add(new Paragraph("Balance").setBold()).setTextAlignment(TextAlignment.CENTER));
            // Data
            BigDecimal totalPrincipal = BigDecimal.ZERO;
            BigDecimal totalInterest = BigDecimal.ZERO;
            BigDecimal totalPayment = BigDecimal.ZERO;
            for (Payment payment : schedule) {
                BigDecimal principal = payment.getPrincipalPortion().setScale(2, RoundingMode.HALF_UP);
                BigDecimal interest = payment.getInterestPortion().setScale(2, RoundingMode.HALF_UP);
                BigDecimal paymentAmount = principal.add(interest).setScale(2, RoundingMode.HALF_UP);
                totalPrincipal = totalPrincipal.add(principal);
                totalInterest = totalInterest.add(interest);
                totalPayment = totalPayment.add(paymentAmount);
                table.addCell(new Cell().add(new Paragraph(String.valueOf(payment.getPeriod())))
                        .setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(String.format("$%,.2f", principal)))
                        .setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(String.format("$%,.2f", interest)))
                        .setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(String.format("$%,.2f", paymentAmount)))
                        .setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(String.format("$%,.2f", 
                        payment.getRemainingBalance().setScale(2, RoundingMode.HALF_UP))))
                        .setTextAlignment(TextAlignment.CENTER));
            }
            // Totals row
            table.addCell(new Cell().add(new Paragraph("Totals").setBold()).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(String.format("$%,.2f", totalPrincipal)).setBold())
                    .setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(String.format("$%,.2f", totalInterest)).setBold())
                    .setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(String.format("$%,.2f", totalPayment)).setBold())
                    .setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph("")).setTextAlignment(TextAlignment.CENTER)); // No total for Balance
            document.add(table);
            logger.info("Exported schedule to PDF: {}", filePath);
        } catch (Exception e) {
            logger.error("Failed to export PDF to {}: {}", filePath, e.getMessage());
            throw new IOException("Failed to export PDF: " + e.getMessage());
        }
    }
}