package com.project.expensetracker.dto;


public record UserConfigDto(String language, Long paymentModeId, Long defaultAccountId) {
}
