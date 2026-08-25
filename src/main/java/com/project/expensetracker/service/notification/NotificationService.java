package com.project.expensetracker.service.notification;

import com.project.expensetracker.dto.JobStatusDto;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface NotificationService {
    void send(JobStatusDto statusDto);

    void openConnection(String jobId);

    SseEmitter get(String jobId);

    void closeConnection(String string);
}
