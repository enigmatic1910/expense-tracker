package com.project.expensetracker.dto;

public record TokenRefreshRequest(
        String accessToken,
        String refreshToken
) {
}
