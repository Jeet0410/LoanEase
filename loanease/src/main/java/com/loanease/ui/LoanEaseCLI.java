package com.loanease.ui;

import com.loanease.model.Loan;
import com.loanease.model.Payment;
import com.loanease.service.AmortizationService;
import com.loanease.util.LoanInputValidator;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class LoanEaseCLI {
    private final AmortizationService service = new AmortizationService();
    private final LoanInputValidator validator = new LoanInputValidator();

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

        Loan loan = new Loan(principal, rate, term, frequency, extraPayment);
        validator.validate(loan);
        long loanId = service.saveLoan(loan);
        List<Payment> schedule = service.generateSchedule(loanId);
        System.out.println("Period | Principal | Interest | Balance");
        for (Payment p : schedule) {
            System.out.printf("%d | %.2f | %.2f | %.2f%n", 
                              p.getPeriod(), p.getPrincipalPortion(), p.getInterestPortion(), p.getRemainingBalance());
        }
    }

    public static void main(String[] args) {
        new LoanEaseCLI().start();
    }
}