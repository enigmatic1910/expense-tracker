package com.project.expensetracker.service.transaction.strategy;

import com.project.expensetracker.dto.TransactionDto;
import com.project.expensetracker.dto.TransactionRequestDto;
import com.project.expensetracker.enums.TransactionType;

public interface TransactionTypeStrategy {
    TransactionDto process(TransactionRequestDto requestBody, String userId, OperationType type);
    TransactionType getType();
}
