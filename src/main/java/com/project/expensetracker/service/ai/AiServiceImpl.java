package com.project.expensetracker.service.ai;

import com.project.expensetracker.dto.*;
import com.project.expensetracker.entity.AiParsingTask;
import com.project.expensetracker.entity.User;
import com.project.expensetracker.enums.Status;
import com.project.expensetracker.mapper.AiParseTaskMapper;
import com.project.expensetracker.mapper.TransactionMapper;
import com.project.expensetracker.service.ai.parseTask.ParseTaskService;
import com.project.expensetracker.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RequiredArgsConstructor
@Service
public class AiServiceImpl implements AiService {

    private final ChatClient chatClient;
    private final TransactionMapper transactionMapper;
    private final AtomicInteger requestCounter;
    private final ParseTaskService parseTaskService;
    private final AiParseTaskMapper aiParseTaskMapper;
    private final ObjectMapper mapper;
    private final NotificationService notificationService;

    private static final String PROMPT_TEMPLATE = """
     Rules:
      1. Your job is to parse the raw text from the user which is either related to expense or income.

      2. Based on the type of text decide the 'type' field of output json. Allowed values for 'type' fields are EXPENSE or INCOME.

      3. Field 'transactionDate' is having date format: yyyy-mm-dd. If the user doesn't mention any date then use current date as transactionDate.

      4. If the user uses relative dates (e.g., 'today', 'yesterday'). Get the date from following:
            - Current Year for reference: {year}
        - If Today then use {today}
        - If Yesterday then use {yesterday}
        - Day before yesterday then use {dayBeforeYesterday}
        - If no date then use {today}
        - If date mentioned in raw text then pick that date.

      5. Infer the category of expense from the list: {categories}

      6. Extract description from raw text and don't change or add anything to it.

      7. Extract amount from raw text and don't change or add anything to it. Just convert the string to double representation.

      8. Don't answer anything not related to expense or income related raw text. Simply set the 'errorMessage' field of the output json with "NOT_VALID_INPUT"

      9. Raw Text is in English or Hindi language only.
      """;

    @Override
    public TransactionRequestDto parse(AiInputDto text) {

        throw new UnsupportedOperationException();
    }

    @Override
    public void parse(AiParsingTask parsingTask) {
        int counter = requestCounter.incrementAndGet();
        log.info("Start - parse | Request Counter: {}", counter);

        HashMap<String, Object> variables = new HashMap<>();
        variables.put("year", LocalDate.now().getYear());
        variables.put("today", (LocalDate.now()));
        variables.put("yesterday", (LocalDate.now().minusDays(1)));
        variables.put("dayBeforeYesterday", (LocalDate.now().minusDays(2)));
        variables.put("categories", null);


        SystemPromptTemplate systemPromptTemplate = SystemPromptTemplate.builder()
                .template(PROMPT_TEMPLATE)
                .variables(variables)
                .build();

        AiParseDto result = chatClient.prompt()
                .system(systemPromptTemplate.render())
                .call()
                .entity(AiParseDto.class);

        parsingTask.setStatus(Status.COMPLETED);
        parsingTask.setContent(mapper.writeValueAsString(parsingTask));

        parseTaskService.save(parsingTask);
        notificationService.send(JobStatusDto.of(parsingTask.getId().toString(), parsingTask.getStatus().toString()));

        notificationService.closeConnection(parsingTask.getId().toString());
        log.info("End - parse | Request Counter: {}", counter);
    }

    @Override
    public AiTaskDto save(AiInputDto inputDto, String userId) {

        AiParsingTask parsingTask = AiParsingTask.builder()
                .rawInput(inputDto.rawText())
                .user(User.builder().id(userId).build())
                .status(Status.PENDING)
                .build();

        final var savedTask = parseTaskService.save(parsingTask);
        notificationService.openConnection(parsingTask.getId().toString());
        return aiParseTaskMapper.toDto(savedTask, "AI task saved");
    }
}
