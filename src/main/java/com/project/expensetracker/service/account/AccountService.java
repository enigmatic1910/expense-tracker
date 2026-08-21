package com.project.expensetracker.service.account;

import com.project.expensetracker.entity.Account;

public interface AccountService {
    boolean existsByUserAndAccount(String userId, Long accountId);

    void updateBalance(Long accountId, Double amount, Long paymentedModeId, String type);

    Account get(Long accountId);

    void update(Account account);
}
