package com.project.expensetracker.exception;

import java.util.List;

public class AccountNotOwnedByUserException extends RuntimeException {
    public AccountNotOwnedByUserException(List<Long> accounts, String userId) {
        super("Account IDs not owned by the user");
    }
}
