package com.project.expensetracker.controller;

import com.project.expensetracker.entity.Bank;
import com.project.expensetracker.repo.BankRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/banks")
@RequiredArgsConstructor
public class BankController {

    private final BankRepo bankRepo;

    @GetMapping("/all")
    public ResponseEntity<List<Bank>> getAllBanks() {
        final var banks = bankRepo.findAll();
        return ResponseEntity.ok(banks);
    }
}
