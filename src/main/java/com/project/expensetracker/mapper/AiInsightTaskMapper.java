package com.project.expensetracker.mapper;

import com.project.expensetracker.dto.AiInsightDto;
import com.project.expensetracker.entity.AiInsightTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AiInsightTaskMapper {

    @Mapping(source = "createdAt", target = "generatedAt")
    @Mapping(target = "topSpendingCategory", expression = "java(toTopCategory(aiInsightTask))")
    @Mapping(target = "anomalies", expression = "java(splitValues(aiInsightTask.getAnomalies()))")
    @Mapping(target = "actionableTips", expression = "java(splitValues(aiInsightTask.getActionableTips()))")
    @Mapping(target = "status", expression = "java(aiInsightTask.getStatus() != null ? aiInsightTask.getStatus().name() : null)")
    AiInsightDto toDto(AiInsightTask aiInsightTask);

    default AiInsightDto.TopCategory toTopCategory(AiInsightTask entity) {
        if (entity.getTopSpendingCategory() == null
                && entity.getTopSpendingPercentage() == null
                && entity.getTopSpendingInsight() == null) {
            return null;
        }

        return new AiInsightDto.TopCategory(
                entity.getTopSpendingCategory(),
                entity.getTopSpendingPercentage() != null
                        ? entity.getTopSpendingPercentage().doubleValue()
                        : null,
                entity.getTopSpendingInsight()
        );
    }

    default List<String> splitValues(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }

        return Arrays.stream(value.split(";"))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList();
    }
}
