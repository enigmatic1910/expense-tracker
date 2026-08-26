package com.project.expensetracker.dto;

import java.time.LocalDate;

public record AiParseResult(
        String transactionType,
        Double amount,
        String description,
        LocalDate transactionDate,
        String errorMessage,
        String category
) {
}
