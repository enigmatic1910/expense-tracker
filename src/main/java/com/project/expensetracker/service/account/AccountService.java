package com.project.expensetracker.service.account;

import com.project.expensetracker.entity.Account;

import java.util.List;

public interface AccountService {
    boolean existsByUserAndAccount(String userId, List<Long> accounts);

    void updateBalance(Long accountId, Double amount, Long paymentedModeId, String type, boolean isSourceAccount);

    Account get(Long accountId);

    void update(Account account);
}
