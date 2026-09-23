package com.project.expensetracker.service.account;

import com.project.expensetracker.dto.AccountDto;
import com.project.expensetracker.dto.UserBankAccounts;
import com.project.expensetracker.entity.Account;
import com.project.expensetracker.entity.Bank;
import com.project.expensetracker.entity.User;
import com.project.expensetracker.enums.AccountType;
import com.project.expensetracker.enums.TransactionType;
import com.project.expensetracker.exception.AccountNotFoundException;
import com.project.expensetracker.exception.AccountNotOwnedByUserException;
import com.project.expensetracker.exception.InsufficientBalanceException;
import com.project.expensetracker.exception.UserNotFoundException;
import com.project.expensetracker.repo.BankRepo;
import com.project.expensetracker.repo.AccountRepo;
import com.project.expensetracker.repo.UserRepo;
import com.project.expensetracker.service.paymentMode.PaymentModeService;
import com.project.expensetracker.service.account.strategy.AccountBalanceStrategy;
import com.project.expensetracker.service.account.strategy.AccountBalanceStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepo accountRepo;
    private final BankRepo bankRepo;
    private final UserRepo userRepo;
    private final PaymentModeService paymentModeService;
    private final AccountBalanceStrategyFactory accountBalanceStrategyFactory;

    @Transactional
    @Override
    public boolean existsByUserAndAccount(String userId, List<Long> accounts){
        return accountRepo.existsByUserIdAndAccount(userId, accounts, accounts.size());
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccountDto> getUserAccounts(String userId) {
        return accountRepo.findByUserIdAndActive(userId).stream()
                .map(this::toDto)
                .toList();
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
        return accountRepo.findByIdAndIsActiveTrue(accountId)
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

    @Transactional
    @Override
    public List<AccountDto> addAccounts(String userId, UserBankAccounts accounts) {
        final User user = userRepo.findByEmail(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (accounts == null || accounts.accounts() == null || accounts.accounts().isEmpty()) {
            return List.of();
        }

        final List<AccountDto> createdAccounts = new ArrayList<>();

        for (AccountDto accountDto : accounts.accounts()) {
            if (accountDto == null) {
                continue;
            }

            final AccountType accountType = resolveAccountType(accountDto.type());
            if (accountType == AccountType.CASH
                    && accountRepo.existsByUser_EmailAndAccountTypeAndIsActiveTrue(userId, AccountType.CASH)) {
                throw new IllegalArgumentException("You can have only one active cash account");
            }
            final String bankName = accountDto.bankName() == null ? "" : accountDto.bankName().trim();
            if (accountType != AccountType.CASH && bankName.isEmpty()) {
                throw new IllegalArgumentException("Bank name is required");
            }

            final String lastFourDigits = accountDto.lastFourDigits() == null ? "" : accountDto.lastFourDigits().trim();
            if (accountType != AccountType.CASH
                    && (lastFourDigits.length() != 4 || !lastFourDigits.chars().allMatch(Character::isDigit))) {
                throw new IllegalArgumentException("Last four digits must be exactly 4 numbers");
            }

            final Double amount = accountDto.amount() == null ? 0D : accountDto.amount();

            Bank bank = accountType == AccountType.CASH
                    ? null
                    : bankRepo.findByName(bankName).orElseGet(() ->
                    bankRepo.save(Bank.builder().name(bankName).build()));

            final Account savedAccount = accountRepo.save(Account.builder()
                    .bank(bank)
                    .user(user)
                    .lastFourDigits(lastFourDigits)
                    .balance(amount)
                    .accountType(accountType)
                    .isActive(true)
                    .build());

            createdAccounts.add(toDto(savedAccount));
        }

        return createdAccounts;
    }

    @Transactional(readOnly = true)
    @Override
    public AccountDto getUserAccountDetails(String userId, String accountId) {
        final User user = userRepo.findByEmail(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        final Account account = accountRepo.findById(Long.valueOf(accountId))
                .orElseThrow(() -> new AccountNotFoundException(Long.valueOf(accountId)));

        if (account.getUser() == null || !user.getId().equals(account.getUser().getId())) {
            throw new AccountNotOwnedByUserException(List.of(account.getId()), userId);
        }

        return toDto(account);
    }

    @Override
    public Account getAccount(String userId, Long id) {
        return accountRepo.findByIdAndUserId(id, userId);
    }

    @Transactional
    @Override
    public void deleteAccount(String userId, Long accountId) {
        final var account = accountRepo.findByIdAndUserId(accountId, userId);

        if (account == null) {
            throw new AccountNotFoundException(accountId);
        }

        account.setActive(false);
        accountRepo.save(account);
    }

    private AccountType resolveAccountType(String type) {
        if (type == null || type.isBlank()) {
            return AccountType.SAVINGS;
        }

        final String normalized = type.trim().toUpperCase(Locale.ROOT).replace(" ", "_");
        return switch (normalized) {
            case "SAVINGS" -> AccountType.SAVINGS;
            case "CREDIT" -> AccountType.CREDIT;
            case "CASH" -> AccountType.CASH;
            default -> AccountType.valueOf(normalized);
        };
    }

    private AccountDto toDto(Account account) {
        final Bank bank = account.getBank() == null
                ? null
                : Bank.builder()
                .id(account.getBank().getId())
                .name(account.getBank().getName())
                .build();

        return new AccountDto(
                account.getId() == null ? null : String.valueOf(account.getId()),
                bank == null ? null : bank.getName(),
                account.getLastFourDigits(),
                account.getAccountType() == null ? null : account.getAccountType().name(),
                account.getBalance(),
                bank
        );
    }
}
