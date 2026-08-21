package com.project.expensetracker.service.transaction;

import com.project.expensetracker.dto.CreateTransactionDto;
import com.project.expensetracker.dto.TransactionDto;
import com.project.expensetracker.dto.UpdateTransactionDto;

import java.util.List;

public interface TransactionService {
    TransactionDto saveTransaction(CreateTransactionDto requestBody, String userId);

    List<TransactionDto> getAllTransaction(String userId);

    TransactionDto updateTransaction(String userid, UpdateTransactionDto requestBody);

    void deleteTransaction(Long transactionId, String userId);
}
