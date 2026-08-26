package com.project.expensetracker.controller;

import com.project.expensetracker.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/status/{jobId}")
    ResponseEntity<?> getNotificationStatus(@PathVariable String jobId){
        SseEmitter sseEmitter = notificationService.get(jobId);
        return ResponseEntity.ok(sseEmitter);
    }
}
