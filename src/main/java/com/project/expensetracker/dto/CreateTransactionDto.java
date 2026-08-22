package com.project.expensetracker.dto;

import java.time.LocalDateTime;

public record CreateTransactionDto(
        String transactionType,
        Double amount,
        String description,
        Long paymentModeId,
        Long categoryId,
        Long accountId,
        LocalDateTime transactionDate,
        Long toAccount,
        String transferId
) {
}
