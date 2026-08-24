package com.project.expensetracker.dto;

import java.time.LocalDate;

public record TransactionRequestDto(
        Long transactionId,
        String transactionType,
        Double amount,
        String description,
        Long paymentModeId,
        Long categoryId,
        Long accountId,
        LocalDate transactionDate,
        Long toAccount,
        String transferId
) {
}
