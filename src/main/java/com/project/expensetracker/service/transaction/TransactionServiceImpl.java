package com.project.expensetracker.service.transaction;
import com.project.expensetracker.dto.TransactionRequestDto;
import com.project.expensetracker.entity.*;
import com.project.expensetracker.enums.TransactionType;
import com.project.expensetracker.dto.TransactionDto;
import com.project.expensetracker.exception.CategoryNotFoundException;
import com.project.expensetracker.exception.PaymentModeNotFoundException;
import com.project.expensetracker.exception.TransactionNotFoundException;
import com.project.expensetracker.exceptions.AccountNotOwnedByUserException;
import com.project.expensetracker.mapper.TransactionMapper;
import com.project.expensetracker.repo.TransactionRepo;
import com.project.expensetracker.service.account.AccountService;
import com.project.expensetracker.service.category.CategoryService;
import com.project.expensetracker.service.paymentMode.PaymentModeService;
import com.project.expensetracker.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final UserService userService;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final PaymentModeService paymentModeService;
    private final TransactionRepo transactionRepo;
    private final TransactionMapper transactionMapper;

    @Transactional
    @Override
    public TransactionDto saveTransaction(TransactionRequestDto requestBody, String userId) {

        Long accountId = requestBody.accountId();
        Long categoryId = requestBody.categoryId();
        Long paymentModeId = requestBody.paymentModeId();
        String transactionType = requestBody.transactionType().toUpperCase();
        Long toAccountId = requestBody.toAccount();

        List<Long> accountIds = getAccountIds(accountId, transactionType, toAccountId);

        validateAccountCategoryAndPaymentMode(userId, accountIds, categoryId, paymentModeId);

        if(TransactionType.valueOf(transactionType) == TransactionType.TRANSFER){
            return handleTransfer(requestBody, userId, accountId, paymentModeId, toAccountId, transactionType, categoryId);
        }

        return handleExpenseOrIncome(requestBody, userId, accountId, paymentModeId, transactionType, categoryId);
    }

    private TransactionDto handleTransfer(TransactionRequestDto requestBody, String userId, Long accountId, Long paymentModeId, Long toAccountId, String transactionType, Long categoryId){
        accountService.updateBalance(accountId, requestBody.amount(), paymentModeId, requestBody.transactionType(), true);

        accountService.updateBalance(toAccountId, requestBody.amount(), paymentModeId, requestBody.transactionType(), false);

        final var transeferId = UUID.randomUUID().toString();

        final var debitTransaction = Transaction.builder()
                .user(User.builder()
                        .id(userId)
                        .build())
                .account(Account.builder()
                        .id(accountId)
                        .build())
                .transactionType(TransactionType.valueOf(transactionType))
                .amount(-requestBody.amount())
                .transferId(transeferId)
                .category(Category.builder()
                        .id(categoryId)
                        .build())
                .paymentMode(PaymentMode.builder()
                        .id(paymentModeId)
                        .build())
                .description(requestBody.description())
                .transactionDate(requestBody.transactionDate())
                .build();

        final var creditTransaction = Transaction.builder()
                .user(User.builder()
                        .id(userId)
                        .build())
                .account(Account.builder()
                        .id(toAccountId)
                        .build())
                .transferId(transeferId)
                .transactionType(TransactionType.valueOf(transactionType))
                .amount(requestBody.amount())
                .category(Category.builder()
                        .id(categoryId)
                        .build())
                .paymentMode(PaymentMode.builder()
                        .id(paymentModeId)
                        .build())
                .description(requestBody.description())
                .transactionDate(requestBody.transactionDate())
                .build();
        transactionRepo.save(debitTransaction);
        final var savedTransaction = transactionRepo.save(creditTransaction);
        return transactionMapper.toTransactionDto(savedTransaction);
    }

    private TransactionDto handleExpenseOrIncome(TransactionRequestDto requestBody, String userId, Long accountId, Long paymentModeId, String transactionType, Long categoryId) {
        accountService.updateBalance(accountId, requestBody.amount(), paymentModeId, requestBody.transactionType(), false);

        final var transaction = Transaction.builder()
                .user(User.builder()
                        .id(userId)
                        .build())
                .transactionType(TransactionType.valueOf(transactionType))
                .amount((TransactionType.valueOf(transactionType) == TransactionType.EXPENSE) ? -requestBody.amount() : requestBody.amount())
                .category(Category.builder()
                        .id(categoryId)
                        .build())
                .paymentMode(PaymentMode.builder()
                        .id(paymentModeId)
                        .build())
                .description(requestBody.description())
                .transactionDate(requestBody.transactionDate())
                .build();

        final var savedTransaction = transactionRepo.save(transaction);
        return transactionMapper.toTransactionDto(savedTransaction);
    }

    private static @NonNull List<Long> getAccountIds(Long accountId, String transactionType, Long toAccountId) {
        List<Long> accountIds = new ArrayList<>();
        accountIds.add(accountId);

        if(TransactionType.valueOf(transactionType) == TransactionType.TRANSFER){
            accountIds.add(toAccountId);
        }
        return accountIds;
    }

    private void validateAccountCategoryAndPaymentMode(String userId, List<Long> accounts, Long categoryId, Long paymentedModeId) {
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
    }

    @Override
    public List<TransactionDto> getAllTransaction(String userId) {

        List<Transaction> transactions = transactionRepo.findAllByUserId(userId);

        return transactionMapper.toTransactionDtos(transactions);
    }

    @Override
    public TransactionDto updateTransaction(String userId, TransactionRequestDto requestBody) {

        final var accountId = requestBody.accountId();
        final var categoryId = requestBody.categoryId();
        final var paymentModeId = requestBody.paymentModeId();
        final var transactionType = requestBody.transactionType().toUpperCase();
        final var toAccountId = requestBody.toAccount();

        List<Long> accountIds = getAccountIds(accountId, transactionType, toAccountId);

        validateAccountCategoryAndPaymentMode(userId, accountIds, categoryId, paymentModeId);

        final Transaction transaction = transactionRepo.findById(requestBody.transactionId())
                .orElseThrow(() -> new TransactionNotFoundException(requestBody.transactionId()));


        transactionMapper.updateTransactionFromDto(requestBody, transaction, userId);

        final var savedTransaction = transactionRepo.save(transaction);

        return transactionMapper.toTransactionDto(savedTransaction);

    }

    @Override
    public void deleteTransaction(Long transactionId, String userId) {
        transactionRepo.deleteByIdAndUserId(transactionId, userId);
    }
}
