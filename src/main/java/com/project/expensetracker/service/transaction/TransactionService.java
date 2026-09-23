package com.project.expensetracker.service.transaction;

import com.project.expensetracker.dto.TransactionDto;
import com.project.expensetracker.dto.TransactionRequestDto;
import com.project.expensetracker.dto.CategorySpendDto;
import com.project.expensetracker.dto.TransactionSummaryDto;
import com.project.expensetracker.dto.SpendingTrendDto;
import com.project.expensetracker.enums.TransactionType;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface TransactionService {
    TransactionDto saveTransaction(TransactionRequestDto requestBody, String userId);

    List<TransactionDto> getAllTransaction(String userId, Pageable pageable);

    List<TransactionDto> getTransactionsByType(String userId, TransactionType type, Pageable pageable);

    List<CategorySpendDto> getCategorySpend(String userId);

    TransactionSummaryDto getTransactionSummary(String userId);

    List<SpendingTrendDto> getSpendingTrend(String userId, int days);

    TransactionDto updateTransaction(String userid, TransactionRequestDto requestBody);

    void deleteTransaction(Long transactionId, String userId);
}
