package com.project.expensetracker.service.ai.parseTask;

import com.project.expensetracker.entity.AiParsingTask;
import com.project.expensetracker.enums.Status;

import java.util.List;

public interface ParseTaskService {
    AiParsingTask save(AiParsingTask parsingTask);

    List<AiParsingTask> getPendingTasks(Status status);


}
