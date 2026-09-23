package com.project.expensetracker.service.transaction;
import com.project.expensetracker.dto.TransactionRequestDto;
import com.project.expensetracker.entity.*;
import com.project.expensetracker.enums.TransactionType;
import com.project.expensetracker.dto.TransactionDto;
import com.project.expensetracker.dto.CategorySpendDto;
import com.project.expensetracker.dto.TransactionSummaryDto;
import com.project.expensetracker.dto.SpendingTrendDto;
import com.project.expensetracker.exception.CategoryNotFoundException;
import com.project.expensetracker.exception.PaymentModeNotFoundException;
import com.project.expensetracker.exception.AccountNotOwnedByUserException;
import com.project.expensetracker.mapper.TransactionMapper;
import com.project.expensetracker.repo.TransactionRepo;
import com.project.expensetracker.repo.CardRepo;
import com.project.expensetracker.service.account.AccountService;
import com.project.expensetracker.service.category.CategoryService;
import com.project.expensetracker.service.paymentMode.PaymentModeService;
import com.project.expensetracker.service.transaction.strategy.OperationType;
import com.project.expensetracker.service.transaction.strategy.TransactionTypeStrategy;
import com.project.expensetracker.service.transaction.strategy.TxnTypeStrategyFactory;
import com.project.expensetracker.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final UserService userService;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final PaymentModeService paymentModeService;
    private final TransactionRepo transactionRepo;
    private final CardRepo cardRepo;
    private final TransactionMapper transactionMapper;
    private final TxnTypeStrategyFactory txnTypeStrategyFactory;

    @Transactional
    @Override
    public TransactionDto saveTransaction(TransactionRequestDto requestBody, String userId) {

        getAndValidateAccounts(requestBody, userId);

        final var transactionType = TransactionType.valueOf(requestBody.transactionType())==TransactionType.TRANSFER ? TransactionType.TRANSFER : TransactionType.INCOME;

        TransactionTypeStrategy strategy = txnTypeStrategyFactory.type(transactionType);

        return strategy.process(requestBody, userId, OperationType.CREATE);
    }

    private void getAndValidateAccounts(TransactionRequestDto requestBody, String userId){
        Long accountId = requestBody.accountId();
        Long categoryId = requestBody.categoryId();
        Long paymentModeId = requestBody.paymentModeId();
        String transactionType = requestBody.transactionType().toUpperCase();
        Long toAccountId = requestBody.toAccount();

        List<Long> accountIds = getAccountIds(accountId, transactionType, toAccountId);

        validateAccountCategoryAndPaymentMode(userId, requestBody, accountIds, categoryId, paymentModeId);
    }

    private static @NonNull List<Long> getAccountIds(Long accountId, String transactionType, Long toAccountId) {
        List<Long> accountIds = new ArrayList<>();
        accountIds.add(accountId);

        if(TransactionType.valueOf(transactionType) == TransactionType.TRANSFER){
            accountIds.add(toAccountId);
        }
        return accountIds;
    }

    private void validateAccountCategoryAndPaymentMode(String userId, TransactionRequestDto requestBody, List<Long> accounts, Long categoryId, Long paymentedModeId) {
        final var accountExists = accountService.existsByUserAndAccount(userId, accounts);
        if(!accountExists) {
            throw new AccountNotOwnedByUserException(accounts, userId);
        }


        final var categoryExists = categoryService.existByUserAndCategory(userId, categoryId);

        if(!categoryExists) {
            throw new CategoryNotFoundException(categoryId);
        }


        final var paymentModeExists = paymentModeService.existsById(paymentedModeId);

        if(!paymentModeExists) {
            throw new PaymentModeNotFoundException(paymentedModeId);
        }

        if (requestBody.cardId() != null && !requestBody.cardId().isBlank()) {
            final Card card = cardRepo.findActiveByIdAndUserEmail(requestBody.cardId(), userId);
            if (card == null) {
                throw new IllegalArgumentException("Card is not available for this user");
            }
        }
    }

    @Override
    public List<TransactionDto> getAllTransaction(String userId, Pageable pageable) {

        List<Transaction> transactions = transactionRepo.findAllByUserId(userId, pageable).getContent();

        return transactionMapper.toTransactionDtos(transactions);
    }

    @Override
    public List<TransactionDto> getTransactionsByType(String userId, TransactionType type, Pageable pageable) {
        List<Transaction> transactions = transactionRepo
                .findAllByUserIdAndTransactionType(userId, type, pageable)
                .getContent();
        return transactionMapper.toTransactionDtos(transactions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategorySpendDto> getCategorySpend(String userId) {
        return transactionRepo.findCategorySpendByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionSummaryDto getTransactionSummary(String userId) {
        return new TransactionSummaryDto(
                transactionRepo.sumAmountByUserIdAndTransactionType(userId, TransactionType.INCOME),
                transactionRepo.sumAmountByUserIdAndTransactionType(userId, TransactionType.EXPENSE)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpendingTrendDto> getSpendingTrend(String userId, int days) {
        final int range = days == 14 || days == 30 ? days : 7;
        final LocalDate end = LocalDate.now();
        final LocalDate start = end.minusDays(range - 1L);
        final Map<LocalDate, Double> dailySpend = transactionRepo
                .findDailyExpenseSpend(userId, start, end)
                .stream()
                .collect(Collectors.toMap(
                        daily -> daily.date(),
                        daily -> daily.spent() == null ? 0D : daily.spent()
                ));
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d", Locale.ENGLISH);

        return start.datesUntil(end.plusDays(1))
                .map(date -> new SpendingTrendDto(
                        date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
                        dailySpend.getOrDefault(date, 0D),
                        date.format(dateFormatter)
                ))
                .toList();
    }

    @Override
    public TransactionDto updateTransaction(String userId, TransactionRequestDto requestBody) {

        getAndValidateAccounts(requestBody, userId);

        final var transactionType = TransactionType.valueOf(requestBody.transactionType())==TransactionType.TRANSFER ? TransactionType.TRANSFER : TransactionType.INCOME;

        TransactionTypeStrategy strategy = txnTypeStrategyFactory.type(transactionType);
        return strategy.process(requestBody, userId, OperationType.UPDATE);
    }

    @Override
    public void deleteTransaction(Long transactionId, String userId) {
        transactionRepo.deleteByIdAndUserId(transactionId, userId);
    }
}
