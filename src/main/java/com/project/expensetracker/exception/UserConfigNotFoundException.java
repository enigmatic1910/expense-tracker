package com.project.expensetracker.exception;

public class UserConfigNotFoundException extends RuntimeException {
    public UserConfigNotFoundException(String userId) {
        super("User config not found for user ID: " + userId);
    }
}
