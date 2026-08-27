package com.project.expensetracker.dto;

public record LoginRequestDto(
        String email,
        String password
) {
}
