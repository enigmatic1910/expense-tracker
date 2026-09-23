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
import com.project.expensetracker.repo.AccountRepo;
import com.project.expensetracker.repo.PaymentModeRepo;
import com.project.expensetracker.entity.Account;
import com.project.expensetracker.entity.PaymentMode;
import com.project.expensetracker.entity.Transaction;
import com.project.expensetracker.service.ai.parseTask.ParseTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private final AccountRepo accountRepo;
    private final PaymentModeRepo paymentModeRepo;
    private final ParseTaskService parseTaskService;

    private static final Pattern ACCOUNT_NUMBER = Pattern.compile("(?:account|acct)\\s*#?\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

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

        if (event.aiParseTask().getStatus() != com.project.expensetracker.enums.Status.COMPLETED) {
            return;
        }

        String userId = event.aiParseTask().getUser().getId();
        String userEmail = event.aiParseTask().getUser().getEmail();
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

        try {
            final UserConfig userConfig = userConfigService.getByUserId(userEmail);
            AiParseResult aiParseResult = objectMapper.readValue(event.aiParseTask().getContent(), AiParseResult.class);

            if ("NOT_VALID_INPUT".equalsIgnoreCase(aiParseResult.errorMessage())) {
                log.info("Ignoring non-transaction AI input for jobId: {}", event.jobId());
                return;
            }

            log.info("AI parsed category for job {}: [{}]", event.jobId(), aiParseResult.category());
            Category category = resolveCategory(aiParseResult.category(), userId);
            PaymentMode paymentMode = resolvePaymentMode(aiParseResult.paymentMode(), userConfig.getPaymentMode());
            Account account = resolveAccount(aiParseResult.account(), userId, userConfig.getDefaultAccount(), paymentMode);

            final TransactionRequestDto requestDto = transactionMapper.fromAiParseResult(
                    aiParseResult,
                    paymentMode.getId(),
                    account.getId(),
                    category.getId()
            );

            var transaction = transactionService.saveTransaction(requestDto, userId);
            event.aiParseTask().setTransaction(Transaction.builder().id(transaction.transactionId()).build());
            parseTaskService.save(event.aiParseTask());
        } catch (Exception exception) {
            event.aiParseTask().setStatus(com.project.expensetracker.enums.Status.FAILED);
            event.aiParseTask().setErrorMessage(exception.getMessage());
            parseTaskService.save(event.aiParseTask());
            log.error("Could not create transaction for AI task {}", event.jobId(), exception);
        }
    }

    private Category resolveCategory(String aiCategory, String userId) {
        if (aiCategory == null || aiCategory.isBlank()) {
            throw new IllegalArgumentException("AI did not return a category");
        }

        final String categoryName = aiCategory.trim();
        try {
            return categoryService.getByNameForUser(categoryName, userId);
        } catch (IllegalArgumentException ignored) {
            throw new IllegalArgumentException(
                    "AI returned an invalid or combined category: " + categoryName
                            + ". Select one category from the user's category list."
            );
        }
    }

    private Account resolveAccount(String reference, String userId, Account defaultAccount, PaymentMode paymentMode) {
        List<Account> accounts = accountRepo.findAllByUserIdAndIsActiveTrueOrderByCreatedAtAsc(userId);

        if (reference == null || reference.isBlank()) {
            if (paymentMode != null && "cash".equalsIgnoreCase(paymentMode.getName())) {
                return accounts.stream()
                        .filter(a -> a.getAccountType() == com.project.expensetracker.enums.AccountType.CASH)
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No cash account setup. Please create a cash account first."));
            }

            if (defaultAccount == null || !defaultAccount.isActive()) {
                throw new IllegalStateException("The default account is inactive or unavailable. Please select a new default account.");
            }
            return defaultAccount;
        }

        String normalized = reference.trim().toLowerCase(Locale.ROOT);
        Matcher matcher = ACCOUNT_NUMBER.matcher(normalized);
        if (matcher.find()) {
            int ordinal = Integer.parseInt(matcher.group(1));
            if (ordinal > 0 && ordinal <= accounts.size()) {
                return accounts.get(ordinal - 1);
            }
        }

        return accounts.stream()
                .filter(account -> contains(account.getLastFourDigits(), normalized)
                        || (account.getBank() != null && contains(account.getBank().getName(), normalized))
                        || (account.getAccountType() != null && contains(account.getAccountType().name(), normalized)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + reference));
    }

    private PaymentMode resolvePaymentMode(String name, PaymentMode defaultPaymentMode) {
        if (name == null || name.isBlank()) {
            return defaultPaymentMode;
        }
        return paymentModeRepo.findByNameIgnoreCase(name.trim())
                .orElseThrow(() -> new IllegalArgumentException("Payment mode not found: " + name));
    }

    private boolean contains(String value, String requested) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(requested);
    }
}
