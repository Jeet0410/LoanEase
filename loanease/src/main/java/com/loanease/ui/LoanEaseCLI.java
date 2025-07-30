package com.loanease.ui;

import com.loanease.model.Loan;
import com.loanease.model.Payment;
import com.loanease.service.AmortizationService;
import com.loanease.util.ExportUtil;
import com.loanease.util.LoanInputValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class LoanEaseCLI {
    private static final Logger logger = LoggerFactory.getLogger(LoanEaseCLI.class);
    private final AmortizationService service = new AmortizationService();
    private final LoanInputValidator validator = new LoanInputValidator();
    private final ExportUtil exportUtil = new ExportUtil();

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("LoanEase: Enter loan details");
        System.out.print("Principal (CAD): ");
        BigDecimal principal = new BigDecimal(scanner.nextLine());
        System.out.print("Annual Interest Rate (e.g., 5% = 0.05): ");
        BigDecimal rate = new BigDecimal(scanner.nextLine());
        System.out.print("Term (months): ");
        int term = Integer.parseInt(scanner.nextLine());
        System.out.print("Payment Frequency (Monthly): ");
        String frequency = scanner.nextLine();
        System.out.print("Extra Payment per Period (0 if none): ");
        BigDecimal extraPayment = new BigDecimal(scanner.nextLine());

        try {
            Loan loan = new Loan(principal, rate, term, frequency, extraPayment);
            validator.validate(loan);
            long loanId = service.saveLoan(loan);
            List<Payment> schedule = service.generateSchedule(loanId);
            System.out.println("Period | Principal | Interest | Total Payment | Balance");
            for (Payment p : schedule) {
                BigDecimal totalPayment = p.getPrincipalPortion().add(p.getInterestPortion()).setScale(2, BigDecimal.ROUND_HALF_UP);
                System.out.printf("%d | %.2f | %.2f | %.2f | %.2f%n", 
                                  p.getPeriod(), p.getPrincipalPortion(), p.getInterestPortion(), totalPayment, p.getRemainingBalance());
            }

            // Export option for main schedule
            System.out.print("Export schedule to CSV? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                try {
                    exportUtil.exportToCSV(schedule, "schedule_" + loanId + ".csv");
                    System.out.println("Exported to schedule_" + loanId + ".csv");
                } catch (IOException e) {
                    logger.error("Failed to export CSV: {}", e.getMessage());
                    System.out.println("Error exporting CSV: " + e.getMessage());
                }
            }
            System.out.print("Export schedule to PDF? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                try {
                    exportUtil.exportToPDF(loan, schedule, "schedule_" + loanId + ".pdf");
                    System.out.println("Exported to schedule_" + loanId + ".pdf");
                } catch (IOException e) {
                    logger.error("Failed to export PDF: {}", e.getMessage());
                    System.out.println("Error exporting PDF: " + e.getMessage());
                }
            }

            // Scenario option
            System.out.print("Run a scenario with new extra payment or rate? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                System.out.print("New Extra Payment (0 if unchanged): ");
                BigDecimal newExtraPayment = new BigDecimal(scanner.nextLine());
                System.out.print("New Annual Interest Rate (0 if unchanged): ");
                BigDecimal newRate = new BigDecimal(scanner.nextLine());
                AmortizationService.LoanSchedulePair scenarioPair = service.runScenario(loanId, 
                                                                                       newExtraPayment, 
                                                                                       newRate.compareTo(BigDecimal.ZERO) == 0 ? null : newRate);
                List<Payment> scenarioSchedule = scenarioPair.getSchedule();
                Loan scenarioLoan = scenarioPair.getLoan();
                System.out.println("Scenario Schedule:");
                System.out.println("Period | Principal | Interest | Total Payment | Balance");
                for (Payment p : scenarioSchedule) {
                    BigDecimal totalPayment = p.getPrincipalPortion().add(p.getInterestPortion()).setScale(2, BigDecimal.ROUND_HALF_UP);
                    System.out.printf("%d | %.2f | %.2f | %.2f | %.2f%n", 
                                      p.getPeriod(), p.getPrincipalPortion(), p.getInterestPortion(), totalPayment, p.getRemainingBalance());
                }
                // Export option for scenario schedule
                System.out.print("Export scenario schedule to CSV? (y/n): ");
                if (scanner.nextLine().equalsIgnoreCase("y")) {
                    try {
                        exportUtil.exportToCSV(scenarioSchedule, "scenario_" + loanId + ".csv");
                        System.out.println("Exported to scenario_" + loanId + ".csv");
                    } catch (IOException e) {
                        logger.error("Failed to export scenario CSV: {}", e.getMessage());
                        System.out.println("Error exporting scenario CSV: " + e.getMessage());
                    }
                }
                System.out.print("Export scenario schedule to PDF? (y/n): ");
                if (scanner.nextLine().equalsIgnoreCase("y")) {
                    try {
                        exportUtil.exportToPDF(scenarioLoan, scenarioSchedule, "scenario_" + loanId + ".pdf");
                        System.out.println("Exported to scenario_" + loanId + ".pdf");
                    } catch (IOException e) {
                        logger.error("Failed to export scenario PDF: {}", e.getMessage());
                        System.out.println("Error exporting scenario PDF: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error running application: {}", e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new LoanEaseCLI().start();
    }
}