package com.project.expensetracker.dto;

import com.project.expensetracker.entity.Bank;
import com.project.expensetracker.enums.CardType;

public record CardDto(String id, CardType cardType, String lastFourDigits, Long accountId, Long limit, Bank bank) {
}
