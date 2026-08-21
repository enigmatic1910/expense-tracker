package com.project.expensetracker.service.transaction;
import com.project.expensetracker.dto.UpdateTransactionDto;
import com.project.expensetracker.entity.Category;
import com.project.expensetracker.entity.PaymentMode;
import com.project.expensetracker.entity.Transaction;
import com.project.expensetracker.entity.User;
import com.project.expensetracker.enums.TransactionType;
import com.project.expensetracker.exception.AccountNotFoundException;
import com.project.expensetracker.dto.CreateTransactionDto;
import com.project.expensetracker.dto.TransactionDto;
import com.project.expensetracker.exception.CategoryNotFoundException;
import com.project.expensetracker.exception.PaymentModeNotFoundException;
import com.project.expensetracker.exception.TransactionNotFoundException;
import com.project.expensetracker.mapper.TransactionMapper;
import com.project.expensetracker.repo.TransactionRepo;
import com.project.expensetracker.service.account.AccountService;
import com.project.expensetracker.service.category.CategoryService;
import com.project.expensetracker.service.paymentMode.PaymentModeService;
import com.project.expensetracker.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public TransactionDto saveTransaction(CreateTransactionDto requestBody, String userId) {

        Long accountId = requestBody.accountId();
        Long categoryId = requestBody.categoryId();
        Long paymentedModeId = requestBody.paymentModeId();

        validateAccountCategoryAndPaymentMode(userId, accountId, categoryId, paymentedModeId);

        accountService.updateBalance(accountId, requestBody.amount(), paymentedModeId, requestBody.transactionType());

        final var transaction = Transaction.builder()
                .user(User.builder()
                        .id(userId)
                        .build())
                .transactionType(TransactionType.valueOf(requestBody.transactionType().toUpperCase()))
                .amount(requestBody.amount())
                .category(Category.builder()
                        .id(categoryId)
                        .build())
                .paymentMode(PaymentMode.builder()
                        .id(paymentedModeId)
                        .build())
                .description(requestBody.description())
                .transactionDate(requestBody.transactionDate())
                .build();

        final var savedTransaction = transactionRepo.save(transaction);
        return transactionMapper.toTransactionDto(savedTransaction);
   }

    private void validateAccountCategoryAndPaymentMode(String userId, Long accountId, Long categoryId, Long paymentedModeId) {
        final var accountExists = accountService.existsByUserAndAccount(userId, accountId);
        if(!accountExists) {
            throw new AccountNotFoundException(accountId);
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
    public TransactionDto updateTransaction(String userId, UpdateTransactionDto requestBody) {

        final var accountId = requestBody.accountId();
        final var categoryId = requestBody.categoryId();
        final var paymentModeId = requestBody.paymentModeId();

        validateAccountCategoryAndPaymentMode(userId, accountId, categoryId, paymentModeId);

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
