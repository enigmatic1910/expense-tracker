package com.project.expensetracker.dto;

import java.time.LocalDateTime;

public record UpdateTransactionDto(
        Long transactionId,
        String transactionType,
        Long amount,
        String description,
        Long paymentModeId,
        Long categoryId,
        Long accountId,
        LocalDateTime transactionDate
) {
}
