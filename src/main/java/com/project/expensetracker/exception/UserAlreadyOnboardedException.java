package com.project.expensetracker.exception;

public class UserAlreadyOnboardedException extends RuntimeException {
    public UserAlreadyOnboardedException(String userId) {
        super("User with ID " + userId + " is already onboarded.");
    }
}
