package com.project.expensetracker.dto;

import java.time.LocalDateTime;

public record JobStatusDto(
        String jobId,
        String status,
        String timeStamp
) {
    public static JobStatusDto of(String jobId, String status){
        return new JobStatusDto(jobId, status, LocalDateTime.now().toString());
    }
}
