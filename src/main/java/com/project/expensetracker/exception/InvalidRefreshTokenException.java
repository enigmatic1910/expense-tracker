package com.project.expensetracker.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(String s) {
        super(s);
    }
}
