package com.project.expensetracker.service.transaction;
import com.project.expensetracker.dto.TransactionRequestDto;
import com.project.expensetracker.entity.*;
import com.project.expensetracker.enums.TransactionType;
import com.project.expensetracker.dto.TransactionDto;
import com.project.expensetracker.exception.CategoryNotFoundException;
import com.project.expensetracker.exception.PaymentModeNotFoundException;
import com.project.expensetracker.exception.AccountNotOwnedByUserException;
import com.project.expensetracker.mapper.TransactionMapper;
import com.project.expensetracker.repo.TransactionRepo;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final UserService userService;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final PaymentModeService paymentModeService;
    private final TransactionRepo transactionRepo;
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

        validateAccountCategoryAndPaymentMode(userId, accountIds, categoryId, paymentModeId);
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
