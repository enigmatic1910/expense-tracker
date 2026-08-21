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
        final var account = Account.builder()
                .user(savedUser)
                .build();

        final var savedAccount = accountRepo.save(account);

        final var userId = user.getId();
        //final var userId = UUID.randomUUID().toString();
        final var accountId = account.getId();

        assertTrue(accountRepo.existsByUserIdAndAccount(userId, accountId));
    }
}
