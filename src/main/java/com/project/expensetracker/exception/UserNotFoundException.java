package com.project.expensetracker.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super("User with email " + email + " not found. Register first or check the email.");
    }
}
