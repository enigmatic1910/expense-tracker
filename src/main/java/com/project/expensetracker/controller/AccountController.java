package com.project.expensetracker.controller;

import com.project.expensetracker.dto.AccountDto;
import com.project.expensetracker.dto.UserBankAccounts;
import com.project.expensetracker.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/all")
    ResponseEntity<List<AccountDto>> getAllAccounts(@AuthenticationPrincipal String userId) {
        final var accounts = accountService.getUserAccounts(userId);
        return ResponseEntity.ok(accounts);
    }

//    @GetMapping(version = "2")
//    public ResponseEntity<List<AccountDto>> getUserAccountsV2(@AuthenticationPrincipal String userId, @RequestParam(required = false) String paymentMode) {
//        final var accounts = accountService.getUserAccountsV2(userId, paymentMode);
//        return ResponseEntity.ok(accounts);
//    }

    @PostMapping
    public ResponseEntity<List<AccountDto>> addAccounts(@AuthenticationPrincipal String userId, @RequestBody UserBankAccounts accounts) {
        final var createdAccounts = accountService.addAccounts(userId, accounts);
        return ResponseEntity.ok(createdAccounts);
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDto> getUserAccountDetails(@AuthenticationPrincipal String userId, @PathVariable String accountId) {
        final var account = accountService.getUserAccountDetails(userId, accountId);
        return ResponseEntity.ok(account);
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal String userId, @PathVariable Long accountId) {
        accountService.deleteAccount(userId, accountId);
        return ResponseEntity.ok().build();
    }
}
