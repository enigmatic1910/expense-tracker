package com.project.expensetracker.dto;

public record ApiResponse (
        int status,
        String message,
        String timestamp
) {
}
