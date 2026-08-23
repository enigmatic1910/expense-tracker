package com.project.expensetracker.dto;

import java.time.LocalDateTime;

public record TransactionDto(
        Long transactionId,
        String transactionType,
        Double amount,
        String description,
        LocalDateTime transactionDate,
        String transferId,
        Long paymentModeId,
        Long categoryId

) {
}
