package com.project.expensetracker.exception;

public class PaymentModeNotFoundException extends RuntimeException {
    public PaymentModeNotFoundException(Long paymentModeId) {
        super("Payment mode with id " + paymentModeId + " not found");
    }
}
