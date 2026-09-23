package com.project.expensetracker.dto;

import java.time.LocalDate;

public record TransactionDailySpendDto(
        LocalDate date,
        Double spent
) {
}
