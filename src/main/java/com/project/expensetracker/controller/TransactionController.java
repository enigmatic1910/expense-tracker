package com.project.expensetracker.controller;

import com.project.expensetracker.dto.CreateTransactionDto;
import com.project.expensetracker.dto.TransactionDto;
import com.project.expensetracker.dto.UpdateTransactionDto;
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

    private static final String LOGGED_IN_USER = "99b21675-bf67-42a6-925e-c5a7ccda0b56";
    private final TransactionService transactionService;

    @PostMapping("/")
    ResponseEntity<TransactionDto> createTransaction(@RequestBody CreateTransactionDto requestBody) {

        final var responseBody = transactionService.saveTransaction(requestBody, LOGGED_IN_USER);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @GetMapping("/getList")
    ResponseEntity<List<TransactionDto>> getAllTransactions() {

        List<TransactionDto> transactions = transactionService.getAllTransaction(LOGGED_IN_USER);
        return ResponseEntity.ok(transactions);
    }

    @PatchMapping("/")
    ResponseEntity<TransactionDto> updateTransaction(@RequestBody UpdateTransactionDto requestBody) {

        var responseBody = transactionService.updateTransaction(LOGGED_IN_USER, requestBody);
        return ResponseEntity.ok(responseBody);
    }

    @DeleteMapping("/delete/{transactionId}")
    ResponseEntity<Void> deleteTransaction(@PathVariable Long transactionId) {
        transactionService.deleteTransaction(transactionId, LOGGED_IN_USER);
        return ResponseEntity.noContent().build();
    }
}
