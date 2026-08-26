package com.project.expensetracker.event;

import com.project.expensetracker.dto.AiParseResult;
import com.project.expensetracker.dto.JobStatusDto;
import com.project.expensetracker.dto.TransactionRequestDto;
import com.project.expensetracker.entity.Category;
import com.project.expensetracker.entity.UserConfig;
import com.project.expensetracker.mapper.TransactionMapper;
import com.project.expensetracker.service.category.CategoryService;
import com.project.expensetracker.service.notification.NotificationService;
import com.project.expensetracker.service.transaction.TransactionService;
import com.project.expensetracker.service.userConfig.UserConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventHandler {

    private final NotificationService notificationService;
    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final TransactionMapper transactionMapper;
    private final UserConfigService userConfigService;
    private final ObjectMapper objectMapper;

    @Async
    @EventListener(AiParsingTaskCompleted.class)
    public void notifyClient(AiParsingTaskCompleted task) {
        log.info("AiParsing task completed for jobId: {} with status: {}", task.jobId(), task.aiParseTask().getStatus().name());
        notificationService.send(JobStatusDto.of(task.jobId(), task.aiParseTask().getStatus().name()));
        notificationService.closeConnection(task.jobId());
    }

    @EventListener(AiParsingTaskCreated.class)
    public void openConnection(AiParsingTaskCreated task) {
        log.info("AiParsing task created for jobId: {}", task.jobId());
        notificationService.openConnection(task.jobId());
    }

    @EventListener(AiParsingTaskCompleted.class)
    public void saveParsingResultAsTxn(AiParsingTaskCompleted event) {
        log.info("Task completed, saving it to db");

        String userId = event.aiParseTask().getUser().getId();
//        Transaction transaction = task.getTransaction();
//
//        TransactionRequestDto requestDto = new TransactionRequestDto(
//                transaction.getId(),
//                transaction.getTransactionType().name(),
//                transaction.getAmount(),
//                transaction.getDescription(),
//                transaction.getPaymentMode().getId(),
//                transaction.getCategory().getId(),
//                transaction.getAccount().getId(),
//                LocalDate.now(),
//                null,
//                transaction.getTransferId()
//        );

        final UserConfig userConfig = userConfigService.getByUserId(userId);

        AiParseResult aiParseResult = objectMapper.readValue(event.aiParseTask().getContent(), AiParseResult.class);

        Category category = categoryService.getByName(aiParseResult.category());

        final TransactionRequestDto requestDto = transactionMapper.fromAiParseResult(
                aiParseResult,
                userConfig.getPaymentMode().getId(),
                userConfig.getDefaultAccount().getId(),
                category.getId()
        );

        transactionService.saveTransaction(requestDto, userId);
    }
}
