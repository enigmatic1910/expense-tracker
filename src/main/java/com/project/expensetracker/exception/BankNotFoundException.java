package com.project.expensetracker.exception;

import java.util.UUID;

public class BankNotFoundException extends RuntimeException {
    public BankNotFoundException(UUID bankId){
        super("Bank with ID " + bankId + " not found.");
    }
}
