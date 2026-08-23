package com.project.expensetracker.controller;

import com.project.expensetracker.dto.TransactionDto;
import com.project.expensetracker.dto.TransactionRequestDto;
import com.project.expensetracker.service.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private static final String LOGGED_IN_USER = "674a1664-ce08-47fc-a055-22c109216b78";
    private final TransactionService transactionService;

    @PostMapping("/")
    ResponseEntity<TransactionDto> createTransaction(@RequestBody TransactionRequestDto requestBody) {

        final var responseBody = transactionService.saveTransaction(requestBody, LOGGED_IN_USER);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @GetMapping("/getList")
    ResponseEntity<List<TransactionDto>> getAllTransactions() {

        List<TransactionDto> transactions = transactionService.getAllTransaction(LOGGED_IN_USER);
        return ResponseEntity.ok(transactions);
    }

    @PatchMapping("/")
    ResponseEntity<TransactionDto> updateTransaction(@RequestBody TransactionRequestDto requestBody) {

        var responseBody = transactionService.updateTransaction(LOGGED_IN_USER, requestBody);
        return ResponseEntity.ok(responseBody);
    }

    @DeleteMapping("/delete/{transactionId}")
    ResponseEntity<Void> deleteTransaction(@PathVariable Long transactionId) {
        transactionService.deleteTransaction(transactionId, LOGGED_IN_USER);
        return ResponseEntity.noContent().build();
    }
}
