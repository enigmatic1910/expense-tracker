package com.project.expensetracker.service.transaction;

import com.project.expensetracker.dto.TransactionDto;
import com.project.expensetracker.dto.TransactionRequestDto;

import java.util.List;

public interface TransactionService {
    TransactionDto saveTransaction(TransactionRequestDto requestBody, String userId);

    List<TransactionDto> getAllTransaction(String userId);

    TransactionDto updateTransaction(String userid, TransactionRequestDto requestBody);

    void deleteTransaction(Long transactionId, String userId);
}
