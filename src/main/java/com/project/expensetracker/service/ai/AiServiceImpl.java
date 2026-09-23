package com.project.expensetracker.service.ai;

import com.project.expensetracker.dto.*;
import com.project.expensetracker.entity.AiParsingTask;
import com.project.expensetracker.entity.AiInsightTask;
import com.project.expensetracker.entity.User;
import com.project.expensetracker.enums.Status;
import com.project.expensetracker.exception.InsightGenerationLimitException;
import com.project.expensetracker.event.AiParsingTaskCompleted;
import com.project.expensetracker.event.AiParsingTaskCreated;
import com.project.expensetracker.event.EventHandler;
import com.project.expensetracker.mapper.AiParseTaskMapper;
import com.project.expensetracker.mapper.AiInsightTaskMapper;
import com.project.expensetracker.repo.AiInsightRepo;
import com.project.expensetracker.repo.TransactionRepo;
import com.project.expensetracker.repo.UserRepo;
import com.project.expensetracker.service.ai.parseTask.ParseTaskService;
import com.project.expensetracker.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AiServiceImpl implements AiService {

    private final ChatClient chatClient;
    private final ApplicationEventPublisher eventPublisher;
    private final AtomicInteger requestCounter;
    private final ParseTaskService parseTaskService;
    private final AiParseTaskMapper aiParseTaskMapper;
    private final ObjectMapper mapper;
    private final AiInsightRepo aiInsightRepo;
    private final AiInsightTaskMapper aiInsightTaskMapper;
    private final TransactionRepo transactionRepo;
    private final UserRepo userRepo;


    private static final String PROMPT_TEMPLATE = """
     Rules:
      1. Your job is to parse the raw text from the user which is either related to expense or income.

      2. Based on the type of text decide the 'transactionType' field of output json. Allowed values for 'transactionType' fields are EXPENSE or INCOME.

      3. Field 'transactionDate' is having date format: yyyy-mm-dd. If the user doesn't mention any date then use current date as transactionDate.

      4. If the user uses relative dates (e.g., 'today', 'yesterday'). Get the date from following:
            - Current Year for reference: {year}
        - If Today then use {today}
        - If Yesterday then use {yesterday}
        - Day before yesterday then use {dayBeforeYesterday}
        - If no date then use {today}
        - If date mentioned in raw text then pick that date.
        
       5. Infer EXACTLY ONE category of expense from the provided list: {categories}. Do NOT combine categories or make up new ones. Pick the single most relevant category.


      5a. If the raw text mentions an account, extract its human-readable reference in 'account'.
          Examples: 'account 2', 'my SBI account', or the last four digits.
          If no account is mentioned, set 'account' to null.

      5b. If the raw text mentions a payment method, extract it in 'paymentMode'.
          Examples: 'UPI', 'cash', 'debit card'. If none is mentioned, set 'paymentMode' to null.

      6. Extract description from raw text and don't change or add anything to it.

      7. Extract amount from raw text and don't change or add anything to it. Just convert the string to double representation.

      8. Don't answer anything not related to expense or income related raw text. Simply set the 'errorMessage' field of the output json with "NOT_VALID_INPUT"

      9. Raw Text is in English or Hindi language only.
      """;
    private final EventHandler eventHandler;

    private final CategoryService categoryService;


    @Override
    public TransactionRequestDto parse(AiInputDto text) {

        throw new UnsupportedOperationException();
    }

    @Override
    public void parse(AiParsingTask parsingTask) {
        int counter = requestCounter.incrementAndGet();
        log.info("Start - parse | Request Counter: {}", counter);

        String categories = categoryService.getAllForUser(parsingTask.getUser().getId())
                .stream()
                .map(category -> category.getName().toLowerCase())
                .collect(Collectors.joining(", "));
        try {
            HashMap<String, Object> variables = new HashMap<>();
            variables.put("year", LocalDate.now().getYear());
            variables.put("today", (LocalDate.now()));
            variables.put("yesterday", (LocalDate.now().minusDays(1)));
            variables.put("dayBeforeYesterday", (LocalDate.now().minusDays(2)));
            variables.put("categories", categories);

            SystemPromptTemplate systemPromptTemplate = SystemPromptTemplate.builder()
                    .template(PROMPT_TEMPLATE)
                    .variables(variables)
                    .build();

            AiParseResult result = chatClient.prompt()
                    .system(systemPromptTemplate.render())
                    .user(parsingTask.getRawInput())
                    .call()
                    .entity(AiParseResult.class);

            parsingTask.setStatus(Status.COMPLETED);
            parsingTask.setContent(mapper.writeValueAsString(result));
            parseTaskService.save(parsingTask);
            eventPublisher.publishEvent(new AiParsingTaskCompleted(parsingTask.getId().toString(), parsingTask));
            log.info("End - parse | Request Counter: {}", counter);
        } catch (Exception exception) {
            markFailed(parsingTask, exception);
            log.error("AI parsing failed for task {}", parsingTask.getId(), exception);
        }
    }

    private void markFailed(AiParsingTask parsingTask, Exception exception) {
        String message = exception.getMessage() == null ? exception.getClass().getSimpleName() : exception.getMessage();
        parsingTask.setStatus(Status.FAILED);
        parsingTask.setErrorMessage(message);
        parsingTask.setContent(mapper.writeValueAsString(Map.of("errorMessage", message)));
        parseTaskService.save(parsingTask);
        eventPublisher.publishEvent(new AiParsingTaskCompleted(parsingTask.getId().toString(), parsingTask));
    }

    @Override
    public AiTaskDto save(AiInputDto inputDto, String userId) {

        AiParsingTask parsingTask = AiParsingTask.builder()
                .rawInput(inputDto.rawText())
                .user(User.builder().id(userId).build())
                .status(Status.PENDING)
                .build();

        final var savedTask = parseTaskService.save(parsingTask);
        eventPublisher.publishEvent(new AiParsingTaskCreated(savedTask.getId().toString()));
        return aiParseTaskMapper.toDto(savedTask, "AI task saved");
    }

    @Override
    public AiInsightDto getLatestInsight(String userId) {
        return aiInsightRepo.findFirstByAppUserIdOrderByCreatedAtDesc(userId)
                .map(aiInsightTaskMapper::toDto)
                .orElse(null);
    }

    @Override
    public synchronized AiInsightDto generateAiInsightTask(String userEmail) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        long weeklyCount = aiInsightRepo.countByUserEmailAndCreatedAtAfter(
                userEmail, now.minusDays(7));
        long monthlyCount = aiInsightRepo.countByUserEmailAndCreatedAtAfter(
                userEmail, today.withDayOfMonth(1).atStartOfDay());

        if (weeklyCount >= 2) {
            throw new InsightGenerationLimitException(
                    "Insight generation limit reached: maximum 2 insights per 7 days.");
        }
        if (monthlyCount >= 4) {
            throw new InsightGenerationLimitException(
                    "Insight generation limit reached: maximum 4 insights per month.");
        }

        LocalDate periodStart = today.withDayOfMonth(1);
        List<com.project.expensetracker.entity.Transaction> transactions =
                transactionRepo.findAllByUserIdAndTransactionDateBetween(
                        user.getId(), periodStart, today);

        String period = periodStart + " to " + today;
        AiInsightTask task = AiInsightTask.builder()
                .period(period)
                .user(user)
                .status(Status.PROCESSING)
                .build();

        task = aiInsightRepo.save(task);

        try {
            if (transactions.isEmpty()) {
                task.setSummary("No transactions were recorded during this period.");
                task.setAnomalies("");
                task.setActionableTips("Add transactions to receive personalized insights.");
            } else {
                String transactionData = mapper.writeValueAsString(transactions.stream()
                        .map(transaction -> Map.of(
                                "date", String.valueOf(transaction.getTransactionDate()),
                                "type", String.valueOf(transaction.getTransactionType()),
                                "amount", transaction.getAmount() == null ? 0D : Math.abs(transaction.getAmount()),
                                "category", transaction.getCategory() == null
                                        ? "Uncategorized"
                                        : transaction.getCategory().getName(),
                                "description", transaction.getDescription() == null
                                        ? ""
                                        : transaction.getDescription()
                        ))
                        .toList());

                String prompt = """
                        Analyze the user's financial transactions for the period %s.
                        Return only valid JSON matching this schema:
                        {
                          "summary": "string",
                          "topSpendingCategory": "string or null",
                          "topSpendingPercentage": 0.0,
                          "topSpendingInsight": "string or null",
                          "anomalies": ["string"],
                          "actionableTips": ["string"]
                        }
                        Identify spending patterns, unusual transactions, and practical advice.
                        Transactions:
                        %s
                        """.formatted(period, transactionData);

                AiInsightDto result = chatClient.prompt()
                        .user(prompt)
                        .call()
                        .entity(AiInsightDto.class);

                task.setSummary(result.summary());
                if (result.topSpendingCategory() != null) {
                    task.setTopSpendingCategory(result.topSpendingCategory().category());
                    task.setTopSpendingPercentage(result.topSpendingCategory().percentage() == null
                            ? null
                            : result.topSpendingCategory().percentage().floatValue());
                    task.setTopSpendingInsight(result.topSpendingCategory().insight());
                }
                task.setAnomalies(result.anomalies() == null ? "" : String.join("; ", result.anomalies()));
                task.setActionableTips(result.actionableTips() == null ? "" : String.join("; ", result.actionableTips()));
            }

            task.setStatus(Status.COMPLETED);
            task.setCompletedAt(LocalDateTime.now());
        } catch (Exception exception) {
            task.setStatus(Status.FAILED);
            task.setErrorMessage(exception.getMessage() == null
                    ? exception.getClass().getSimpleName()
                    : exception.getMessage());
        }

        AiInsightTask savedTask = aiInsightRepo.save(task);
        return aiInsightTaskMapper.toDto(savedTask);
    }
}
