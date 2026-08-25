package com.project.expensetracker.schedule;

import com.project.expensetracker.dto.JobStatusDto;
import com.project.expensetracker.entity.AiParsingTask;
import com.project.expensetracker.enums.Status;
import com.project.expensetracker.service.ai.AiService;
import com.project.expensetracker.service.ai.parseTask.ParseTaskService;
import com.project.expensetracker.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.PriorityQueue;

@Component
@RequiredArgsConstructor
public class AiTaskScheduler {

    private final ParseTaskService parseTaskService;
    private final AiService aiService;
    private final NotificationService notificationService;

    PriorityQueue<AiParsingTask> taskQueue = new PriorityQueue<>(Comparator.comparing(AiParsingTask :: getCreatedAt));

    @Scheduled(fixedRate = 60000)
    private void scheduleTask(){
        if(taskQueue.isEmpty()){
            final var tasks = parseTaskService.getPendingTasks(Status.PENDING);
            taskQueue.addAll(tasks);
        }

        if(taskQueue.isEmpty()){
            return;
        }

        final var aiParsingTask = taskQueue.remove();
        aiParsingTask.setStatus(Status.PROCESSING);
        parseTaskService.save(aiParsingTask);
        notificationService.send(JobStatusDto.of(aiParsingTask.getId().toString(), aiParsingTask.getStatus().name() ));

        aiService.parse(aiParsingTask);
    }
}
