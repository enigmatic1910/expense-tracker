package com.project.expensetracker.service.ai;

import com.project.expensetracker.dto.AiInputDto;
import com.project.expensetracker.dto.TransactionRequestDto;
import com.project.expensetracker.dto.AiTaskDto;
import com.project.expensetracker.entity.AiParsingTask;

public interface AiService {

    TransactionRequestDto parse(AiInputDto text);

    void parse(AiParsingTask parsingTask);

    AiTaskDto save(AiInputDto inputDto, String userId);
}
