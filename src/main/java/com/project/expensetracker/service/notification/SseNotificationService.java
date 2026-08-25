package com.project.expensetracker.service.notification;

import com.project.expensetracker.dto.JobStatusDto;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseNotificationService implements NotificationService{

    private static final int TIME_OUT_MINUTES = 5;
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    @Override
    public void send(JobStatusDto statusDto) {
        final var emitter = emitters.get(statusDto.jobId());
        try{
            emitter.send(statusDto);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send notification for job: " + statusDto.jobId(), e);
        }

    }

    @Override
    public void openConnection(String jobId) {
        SseEmitter sseEmitter = new SseEmitter(Duration.of(TIME_OUT_MINUTES, ChronoUnit.MINUTES).toMillis());
        emitters.put(jobId, sseEmitter);
    }

    @Override
    public SseEmitter get(String jobId) {
        final var emitter = emitters.get(jobId);
        if(Objects.isNull(emitter)){
            throw new RuntimeException(jobId + " is not found");
        }
        return emitter;
    }

    @Override
    public void closeConnection(String jobId) {
        this.get(jobId).complete();
    }
}
