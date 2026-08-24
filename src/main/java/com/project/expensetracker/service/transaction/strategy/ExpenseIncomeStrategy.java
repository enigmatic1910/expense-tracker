package com.project.expensetracker.service.transaction.strategy;

import com.project.expensetracker.dto.TransactionDto;
import com.project.expensetracker.dto.TransactionRequestDto;
import com.project.expensetracker.entity.Transaction;
import com.project.expensetracker.enums.TransactionType;
import com.project.expensetracker.exception.InsufficientBalanceException;
import com.project.expensetracker.exception.TransactionNotFoundException;
import com.project.expensetracker.mapper.TransactionMapper;
import com.project.expensetracker.repo.TransactionRepo;
import com.project.expensetracker.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("ExpenseIncomeStrategy")
@RequiredArgsConstructor
public class ExpenseIncomeStrategy implements TransactionTypeStrategy {

    private final AccountService accountService;
    private final TransactionMapper transactionMapper;
    private final TransactionRepo transactionRepo;

    @Override
    public TransactionDto process(TransactionRequestDto requestBody, String userId, OperationType type) throws InsufficientBalanceException {
        Transaction transaction = null;
        if(type == OperationType.UPDATE){
            transaction = transactionRepo.findById(requestBody.transactionId())
                    .orElseThrow(() -> new TransactionNotFoundException(requestBody.transactionId()));

            accountService.reverseBalance(requestBody.accountId(), transaction.getAmount(), requestBody.paymentModeId(), requestBody.transactionType(), false);

        }
        else{
            transaction = new Transaction();
        }
        accountService.updateBalance(requestBody.accountId(), requestBody.amount(), requestBody.paymentModeId(), requestBody.transactionType(), false);

        transactionMapper.transactionFromRequestDto(requestBody, transaction, userId, null , true);

        final var savedTransaction = transactionRepo.save(transaction);
        return transactionMapper.toTransactionDto(savedTransaction);
    }

    @Override
    public TransactionType getType() {
        return TransactionType.INCOME;
    }
}
