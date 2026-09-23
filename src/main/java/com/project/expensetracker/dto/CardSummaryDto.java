package com.project.expensetracker.dto;

import com.project.expensetracker.enums.CardType;

public record CardSummaryDto(
        String cardId,
        CardType cardType,
        String lastFourDigits,
        Long creditLimit,
        Double spent
) {
}
