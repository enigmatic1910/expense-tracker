package com.project.expensetracker.service.onboarding;

import com.project.expensetracker.dto.OnboardingRequestDto;
import com.project.expensetracker.entity.Account;
import com.project.expensetracker.entity.Card;
import com.project.expensetracker.entity.User;
import com.project.expensetracker.entity.UserConfig;
import com.project.expensetracker.enums.LanguagePreference;
import com.project.expensetracker.enums.AccountType;
import com.project.expensetracker.exception.BankNotFoundException;
import com.project.expensetracker.exception.PaymentModeNotFoundException;
import com.project.expensetracker.exception.UserAlreadyOnboardedException;
import com.project.expensetracker.exception.UserNotFoundException;
import com.project.expensetracker.repo.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OnboardingServiceImpl implements OnboardingService{

    private final UserConfigRepo userConfigRepo;
    private final UserRepo userRepo;
    private final BankRepo bankRepo;
    private final PaymentModeRepo paymentModeRepo;
    private final AccountRepo accountRepo;
    private final CardRepo cardRepo;

    public OnboardingServiceImpl(UserConfigRepo userConfigRepo, UserRepo userRepo, BankRepo bankRepo, PaymentModeRepo paymentModeRepo, AccountRepo accountRepo, CardRepo cardRepo) {
        this.userConfigRepo = userConfigRepo;
        this.userRepo = userRepo;
        this.bankRepo = bankRepo;
        this.paymentModeRepo = paymentModeRepo;
        this.accountRepo = accountRepo;
        this.cardRepo = cardRepo;
    }

    @Override
    public void onBoard(OnboardingRequestDto request, String userId) {

        if(userConfigRepo.findByEmail(userId).isPresent()){
            throw new UserAlreadyOnboardedException(userId);
        }

        Optional<User> user = userRepo.findByEmail(userId);

        if(!user.isPresent()){
            throw new UserNotFoundException(userId);
        }

        final var bank = bankRepo.findById(request.bankId()).orElseThrow(() -> new BankNotFoundException(request.bankId()));

        final var paymentMode = paymentModeRepo.findById(request.paymentModeId())
                .orElseThrow(() -> new PaymentModeNotFoundException(request.paymentModeId()));

        final var account = Account.builder()
                .bank(bank)
                .lastFourDigits(request.accountLastFourDigits())
                .balance(request.balance())
                .user(user.get())
                .build();

        final var savedBankAccount = accountRepo.save(account);

        if(request.cardType() != null && request.cardLastFourDigits() != null){
            final var card = Card.builder()
                    .account(savedBankAccount)
                    .lastFourDigits(request.cardLastFourDigits())
                    .creditLimit(request.cardLimit() != null ? request.cardLimit() : 0L)
                    .cardType(request.cardType())
                    .build();

            final var savedCard = cardRepo.save(card);
        }

        final var cashAccount = Account.builder()
                .bank(null)
                .lastFourDigits("CASH")
                .balance(request.cashBalance() != null ? request.cashBalance() : 0)
                .accountType(AccountType.CASH)
                .user(user.get())
                .build();

        accountRepo.save(cashAccount);

        final var userConfig = UserConfig.builder()
                .user(user.get())
                .defaultAccount(savedBankAccount)
                .paymentMode(paymentMode)
                .languagePreference(request.languagePreference() != null ? request.languagePreference() : LanguagePreference.ENGLISH)
                .build();

        userConfigRepo.save(userConfig);
        user.get().setOnboarded(true);
        userRepo.save(user.get());
    }
}
