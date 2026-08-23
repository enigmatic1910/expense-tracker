package com.project.expensetracker.service.account.strategy;

import com.project.expensetracker.entity.Account;
import com.project.expensetracker.enums.TransactionBehavior;
import com.project.expensetracker.enums.TransactionType;
import com.project.expensetracker.exception.InsufficientBalanceException;
import org.springframework.stereotype.Component;

@Component("AssetAccountBalanceStrategy")
public class AssestAccountBalanceStrategy implements AccountBalanceStrategy {
    @Override
    public Double calculateBalance(Account account, Double amount, TransactionType transactionType, boolean isSourceAccount) throws InsufficientBalanceException {

        if(transactionType == TransactionType.TRANSFER){
            if(isSourceAccount){
                validateTransaction(account, amount);
                return account.getBalance() - amount;
            }
            return account.getBalance() + amount;
        }

        if(transactionType == TransactionType.EXPENSE){
            validateTransaction(account, amount);
            return account.getBalance() - amount;
        }

        return account.getBalance() + amount;
    }

    @Override
    public Double reverseBalance(Account account, Double previousAmount, TransactionType transactionType, boolean isSourceAccount) {
        if(transactionType == TransactionType.TRANSFER){
            if(isSourceAccount){
                return account.getBalance() + previousAmount;
            }
            return account.getBalance() - previousAmount;
        }

        if(transactionType == TransactionType.EXPENSE){
            return account.getBalance() + previousAmount;
        }
        return account.getBalance() - previousAmount;
    }

    @Override
    public void validateTransaction(Account account, Double amount) throws InsufficientBalanceException {

        Double currentBalance = account.getBalance();
        if(amount > currentBalance) {
            throw new InsufficientBalanceException(account.getId());
        }
    }

    @Override
    public TransactionBehavior getType() {
        return TransactionBehavior.ASSET;
    }
}
