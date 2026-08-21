package com.project.expensetracker.config;


import com.project.expensetracker.entity.Account;
import com.project.expensetracker.entity.Bank;
import com.project.expensetracker.entity.User;
import com.project.expensetracker.repo.AccountRepo;
import com.project.expensetracker.repo.BankRepo;
import com.project.expensetracker.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepo userRepo;
    private final AccountRepo accountRepo;
    private final BankRepo bankRepo;

    @Bean
    CommandLineRunner initData() {
        return args -> {

            if(userRepo.findAll().isEmpty()) {
                var bank = bankRepo.findByName("State Bank of India").orElseThrow();

                var user = User.builder()
                        .name("testuser")
                        .email("testuser@gmail.com")
                        .password("testuser")
                        .build();

                var savedUser = userRepo.save(user);

                var account = Account.builder()
                        .bank(bank)
                        .user(savedUser)
                        .balance(50000.0)
                        .lastFourDigits("0998")
                        .build();

                accountRepo.save(account);

                log.info("Initialized test data: User with account and bank created."+ user.getId());
            }
        };
    }
}
