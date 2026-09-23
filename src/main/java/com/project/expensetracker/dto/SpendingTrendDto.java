package com.project.expensetracker.dto;

public record SpendingTrendDto(
        String day,
        Double spent,
        String date
) {
}
