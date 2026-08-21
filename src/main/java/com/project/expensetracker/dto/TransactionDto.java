package com.project.expensetracker.dto;

import com.project.expensetracker.entity.Category;

import java.time.LocalDateTime;

public record TransactionDto(
        String id,
        String transactionType,
        Double amount,
        String description,
        LocalDateTime transactionDate,
        Long transferId,
        Long paymentModeId,
        Long categoryId

) {
}
