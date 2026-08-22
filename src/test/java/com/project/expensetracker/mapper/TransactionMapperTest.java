package com.project.expensetracker.mapper;


import com.project.expensetracker.dto.TransactionRequestDto;
import com.project.expensetracker.entity.Transaction;
import com.project.expensetracker.enums.TransactionType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionMapperTest {

    @Test
    void shouldReturnTransaction_whenValidTransactionExpenseRequest_isPresent() {

        Long accountId = 1L;
        String transactionType = "EXPENSE";
        Double amount = 100.0;
        String description = "Test transaction";
        LocalDateTime transactionDate = LocalDateTime.now();
        String transferId = null;
        Long paymentModeId = 1L;
        Long categoryId = 1L;

        TransactionRequestDto transactionRequestDto = new TransactionRequestDto(
                null,
                transactionType,
                amount,
                description,
                paymentModeId,
                categoryId,
                accountId,
                transactionDate,
                null,
                transferId
        );

        Transaction transaction = new Transaction();

        TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);
        INSTANCE.transactionFromRequestDto(transactionRequestDto, transaction, "testUserId");

        assertEquals(TransactionType.valueOf(transactionType), transaction.getTransactionType());
        assertEquals(amount, -transaction.getAmount());
        assertEquals(description, transaction.getDescription());
        assertEquals(transactionDate, transaction.getTransactionDate());
        assertEquals(paymentModeId, transaction.getPaymentMode().getId());
        assertEquals(categoryId, transaction.getCategory().getId());
        assertEquals(accountId, transaction.getAccount().getId());
    }

    @Test
    void shouldReturnTransaction_whenValidTransactionIncomeRequest_isPresent() {

        Long accountId = 1L;
        String transactionType = "INCOME";
        Double amount = 100.0;
        String description = "Test transaction";
        LocalDateTime transactionDate = LocalDateTime.now();
        String transferId = null;
        Long paymentModeId = 1L;
        Long categoryId = 1L;

        TransactionRequestDto transactionRequestDto = new TransactionRequestDto(
                null,
                transactionType,
                amount,
                description,
                paymentModeId,
                categoryId,
                accountId,
                transactionDate,
                null,
                transferId
        );

        Transaction transaction = new Transaction();

        TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);
        INSTANCE.transactionFromRequestDto(transactionRequestDto, transaction, "testUserId");

        assertEquals(TransactionType.valueOf(transactionType), transaction.getTransactionType());
        assertEquals(amount, transaction.getAmount());
        assertEquals(description, transaction.getDescription());
        assertEquals(transactionDate, transaction.getTransactionDate());
        assertEquals(paymentModeId, transaction.getPaymentMode().getId());
        assertEquals(categoryId, transaction.getCategory().getId());
        assertEquals(accountId, transaction.getAccount().getId());
    }
}