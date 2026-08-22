package com.project.expensetracker.repo;

import com.project.expensetracker.entity.Account;
import com.project.expensetracker.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class AccountRepoTest {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AccountRepo accountRepo;

    @Test
    void testExistsByUserIdAndAccountId(){
        final var user = User.builder()
                .build();

        final var savedUser = userRepo.save(user);
        final var account1 = Account.builder()
                .user(savedUser)
                .build();

        final var account2 = Account.builder()
                .user(savedUser)
                .build();

        final var savedAccount1 = accountRepo.save(account1);
        final var savedAccount2 = accountRepo.save(account2);

        final var userId = savedUser.getId();

        final var accountId1 = savedAccount1.getId();
        final var accountId2 = savedAccount2.getId();

        assertTrue(accountRepo.existsByUserIdAndAccount(userId, List.of(accountId1, accountId2), 2));
    }
}
