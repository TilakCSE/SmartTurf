package cli;

import java.time.LocalDate;

public class Payment {
    private String paymentId;
    private double amount;
    private String paymentMode; // e.g., "Cash", "UPI", "Credit Card"
    private String paymentDetails; // Details like "Pending" or "Completed"
    private LocalDate paymentDate; // Null if payment is pending

    // Constructor with paymentDetails
    public Payment(String paymentId, double amount, String paymentMode, String paymentDetails) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.paymentMode = paymentMode;
        this.paymentDetails = paymentDetails;
        this.paymentDate = paymentDetails.equalsIgnoreCase("Completed") ? LocalDate.now() : null;
    }

    // Getters
    public String getPaymentId() { return paymentId; }
    public double getAmount() { return amount; }
    public String getPaymentMode() { return paymentMode; }
    public String getPaymentDetails() { return paymentDetails; }
    public LocalDate getPaymentDate() { return paymentDate; }

    // Setter for paymentDetails
    public void setPaymentDetails(String paymentDetails) {
        this.paymentDetails = paymentDetails;
        if (paymentDetails.equalsIgnoreCase("Completed")) {
            this.paymentDate = LocalDate.now(); // Set payment date to current date
        } else {
            this.paymentDate = null; // Set payment date to null if pending
        }
    }
}