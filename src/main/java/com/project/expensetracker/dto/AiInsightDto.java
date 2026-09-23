package com.project.expensetracker.dto;

import com.project.expensetracker.entity.Category;

import java.time.LocalDateTime;
import java.util.List;

public record AiInsightDto(
        String period,
        LocalDateTime generatedAt,
        String summary,
        TopCategory topSpendingCategory,
        List<String> anomalies,
        List<String> actionableTips,
        String status
) {
    public record TopCategory(
            String category,
            Double percentage,
            String insight
    ) {
    }
}
