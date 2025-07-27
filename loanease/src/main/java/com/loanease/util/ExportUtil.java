package com.loanease.util;

import com.loanease.model.Payment;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class ExportUtil {
    public void exportToCSV(List<Payment> schedule, String filePath) throws Exception {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Period,Principal,Interest,Balance\n");
            for (Payment p : schedule) {
                writer.write(String.format("%d,%.2f,%.2f,%.2f\n",
                        p.getPeriod(), p.getPrincipalPortion(), p.getInterestPortion(), p.getRemainingBalance()));
            }
        }
    }

    public void exportToPDF(List<Payment> schedule, String filePath) throws Exception {
        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        document.add(new Paragraph("Loan Amortization Schedule"));
        document.add(new Paragraph("Period | Principal | Interest | Balance"));
        for (Payment p : schedule) {
            document.add(new Paragraph(String.format("%d | %.2f | %.2f | %.2f",
                    p.getPeriod(), p.getPrincipalPortion(), p.getInterestPortion(), p.getRemainingBalance())));
        }
        document.close();
    }
}