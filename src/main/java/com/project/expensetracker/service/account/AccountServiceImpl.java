package com.project.expensetracker.service.account;

import com.project.expensetracker.entity.Account;
import com.project.expensetracker.enums.TransactionType;
import com.project.expensetracker.exception.AccountNotFoundException;
import com.project.expensetracker.exception.InsufficientBalanceException;
import com.project.expensetracker.repo.AccountRepo;
import com.project.expensetracker.service.paymentMode.PaymentModeService;
import com.project.expensetracker.service.account.strategy.AccountBalanceStrategy;
import com.project.expensetracker.service.account.strategy.AccountBalanceStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepo accountRepo;
    private final PaymentModeService paymentModeService;
    private final AccountBalanceStrategyFactory accountBalanceStrategyFactory;

    @Transactional
    @Override
    public boolean existsByUserAndAccount(String userId, List<Long> accounts){
        return accountRepo.existsByUserIdAndAccount(userId, accounts, accounts.size());
    }

    @Transactional
    @Override
    public void updateBalance(Long accountId, Double amount, Long paymentedModeId, String type, boolean isSourceAccount) throws InsufficientBalanceException {
        final var paymentMode = paymentModeService.get(paymentedModeId);

        AccountBalanceStrategy strategy = accountBalanceStrategyFactory.getStrategy((paymentMode.getType()));

        Account account = get(accountId);
        final var updatedBalance = strategy.calculateBalance(account, amount, TransactionType.valueOf(type.toUpperCase()), isSourceAccount);

        account.setBalance(updatedBalance);

        this.update(account);
    }

    @Override
    public Account get(Long accountId) {
        return accountRepo.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    public void update(Account account) {
        accountRepo.save(account);
    }

    @Override
    public void reverseBalance(Long accountId, Double amount, Long paymentModeId, String type, boolean isSourceAccount) {
        final var paymentMode = paymentModeService.get(paymentModeId);

        AccountBalanceStrategy strategy = accountBalanceStrategyFactory.getStrategy((paymentMode.getType()));

        Account account = get(accountId);
        System.out.println(account.getBalance());
        final var updatedBalance = strategy.reverseBalance(account, Math.abs(amount), TransactionType.valueOf(type.toUpperCase()), isSourceAccount);

        account.setBalance(updatedBalance);

        this.update(account);
    }
}
