package com.project.expensetracker.event;

import com.project.expensetracker.entity.AiParsingTask;

public record AiParsingTaskCompleted(
        String jobId,
        AiParsingTask aiParseTask
) {
}
