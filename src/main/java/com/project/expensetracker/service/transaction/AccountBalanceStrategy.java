package com.project.expensetracker.service.transaction;

import com.project.expensetracker.entity.Account;
import com.project.expensetracker.enums.TransactionBehavior;
import com.project.expensetracker.enums.TransactionType;
import com.project.expensetracker.exception.InsufficientBalanceException;

public interface AccountBalanceStrategy {

    Double calculateBalance(Account account, Double amount, TransactionType transactionType, boolean isSourceAccount) throws InsufficientBalanceException;

    Double reverseBalance(Account account, Double previousAmount, TransactionType transactionType, boolean isSourceAccount);

    void validateTransaction(Account account, Double amount);

    TransactionBehavior getType();
}
