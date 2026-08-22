package com.project.expensetracker.service.transaction;

import com.project.expensetracker.enums.TransactionBehavior;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AccountBalanceStrategyFactory {

    public Map<TransactionBehavior, AccountBalanceStrategy> strategies;

    public AccountBalanceStrategyFactory(List<AccountBalanceStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(AccountBalanceStrategy::getType, e -> e));
    }

    public AccountBalanceStrategy getStrategy(TransactionBehavior type) {
        return strategies.get(type);
    }
}
