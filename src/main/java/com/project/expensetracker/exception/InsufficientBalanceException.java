package com.project.expensetracker.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(Long accountId) {
        super("Insufficient balance in the account " + accountId);
    }
}
