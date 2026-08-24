package com.project.expensetracker.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Component
@Slf4j
public class LogRequestCounter {

    private final AtomicInteger requestCounter;

    @Scheduled(fixedRate = 60000)
    public void logAndRestRequestCount(){
        log.info("Reset Counter. Request processed: {} ", requestCounter.getAndSet(0));
    }
}
