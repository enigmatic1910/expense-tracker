package com.project.expensetracker.dto;

public record RegisterRequestDto(
        String username,
        String email,
        String password
) {
}
