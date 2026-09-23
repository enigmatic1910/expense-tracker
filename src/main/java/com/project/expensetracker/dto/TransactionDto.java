package com.project.expensetracker.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionDto(
        Long transactionId,
        String transactionType,
        Double amount,
        String description,
        LocalDate transactionDate,
        String transferId,
        Long paymentModeId,
        Long categoryId,
        String cardId,
        String cardType,
        String cardLastFourDigits

) {
    public TransactionDto(Long transactionId, String transactionType, Double amount, String description,
                          LocalDate transactionDate, String transferId, Long paymentModeId, Long categoryId) {
        this(transactionId, transactionType, amount, description, transactionDate, transferId,
                paymentModeId, categoryId, null, null, null);
    }
}
