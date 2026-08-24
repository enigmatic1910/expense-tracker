package com.project.expensetracker.mapper;

import com.project.expensetracker.dto.AiTaskDto;
import com.project.expensetracker.entity.AiParsingTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AiParseTaskMapper {

    @Mapping(source = "message", target = "message")
    AiTaskDto toDto(AiParsingTask aiParsingTask, String message);
}
