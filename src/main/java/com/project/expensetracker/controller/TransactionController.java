package com.project.expensetracker.controller;

import com.project.expensetracker.dto.TransactionDto;
import com.project.expensetracker.dto.TransactionRequestDto;
import com.project.expensetracker.enums.TransactionType;
import com.project.expensetracker.repo.UserRepo;
import com.project.expensetracker.service.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private static final int RECENT_TRANSACTION_LIMIT = 10;

    private final TransactionService transactionService;
    private final UserRepo userRepo;

    @PostMapping
    ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionRequestDto requestBody, Authentication authentication) {
        String userId = resolveUserId(authentication);

        final var responseBody = transactionService.saveTransaction(requestBody, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @GetMapping
    ResponseEntity<List<TransactionDto>> getAllTransactions(
            @RequestParam(required = false) String type,
            Authentication authentication) {
        String userId = resolveUserId(authentication);
        Pageable recentTransactions = PageRequest.of(
                0,
                RECENT_TRANSACTION_LIMIT,
                Sort.by(
                        Sort.Order.desc("transactionDate"),
                        Sort.Order.desc("id")
                )
        );

        List<TransactionDto> transactions;
        if (type != null && !type.isBlank() && !type.equalsIgnoreCase("all")) {
            TransactionType transactionType = TransactionType.valueOf(type.toUpperCase());
            transactions = transactionService.getTransactionsByType(userId, transactionType, recentTransactions);
        } else {
            transactions = transactionService.getAllTransaction(userId, recentTransactions);
        }

        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/category-spend")
    ResponseEntity<?> getCategorySpend(Authentication authentication) {
        String userId = resolveUserId(authentication);
        return ResponseEntity.ok(transactionService.getCategorySpend(userId));
    }

    @GetMapping("/summary")
    ResponseEntity<?> getTransactionSummary(Authentication authentication) {
        String userId = resolveUserId(authentication);
        return ResponseEntity.ok(transactionService.getTransactionSummary(userId));
    }

    @GetMapping("/spending-trend")
    ResponseEntity<?> getSpendingTrend(
            @RequestParam(defaultValue = "7") int days,
            Authentication authentication
    ) {
        String userId = resolveUserId(authentication);
        return ResponseEntity.ok(transactionService.getSpendingTrend(userId, days));
    }

    @PatchMapping
    ResponseEntity<TransactionDto> updateTransaction(@RequestBody TransactionRequestDto requestBody, Authentication authentication) {
        String userId = resolveUserId(authentication);
        var responseBody = transactionService.updateTransaction(userId, requestBody);
        return ResponseEntity.ok(responseBody);
    }

    @DeleteMapping("/delete/{transactionId}")
    ResponseEntity<Void> deleteTransaction(@PathVariable Long transactionId, Authentication authentication) {
        String userId = resolveUserId(authentication);
        transactionService.deleteTransaction(transactionId, userId);
        return ResponseEntity.noContent().build();
    }

    private String resolveUserId(Authentication authentication) {
        return userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"))
                .getId();
    }
}
