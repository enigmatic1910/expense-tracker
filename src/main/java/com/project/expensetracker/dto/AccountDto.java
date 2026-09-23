package com.project.expensetracker.dto;

import com.project.expensetracker.entity.Bank;

public record AccountDto(
        String id,
        String bankName,
        String lastFourDigits,
        String type,
        Double amount,
        Bank bank
) {
}
