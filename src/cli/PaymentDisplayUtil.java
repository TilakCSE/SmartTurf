package cli;

import Database.PaymentDB;
import java.text.SimpleDateFormat;

public class PaymentDisplayUtil {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MMM-yyyy HH:mm");

    public static void displayPayment(PaymentDB payment) {
        if (payment == null) {
            System.out.println("No payment record found.");
            return;
        }

        System.out.println("\n=== Payment Details ===");
        System.out.println("Payment ID: " + payment.getPaymentId());
        System.out.printf("Amount: ₹%.2f%n", payment.getAmount());
        System.out.println("Mode: " + payment.getPaymentMode());
        System.out.println("Status: " + payment.getPaymentStatus());

        String dateStr = payment.getPaymentDate() != null
                ? DATE_FORMAT.format(payment.getPaymentDate())
                : "Not paid yet";
        System.out.println("Date: " + dateStr);
    }
}