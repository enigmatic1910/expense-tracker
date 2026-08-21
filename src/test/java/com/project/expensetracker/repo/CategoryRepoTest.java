package com.project.expensetracker.repo;

import com.project.expensetracker.entity.Category;
import com.project.expensetracker.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.springframework.test.util.AssertionErrors.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
public class CategoryRepoTest {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CategoryRepo categoryRepo;

    @Test
    public void testExistsByUserIdAndCategoryId(){

        final var user = User.builder()
                .build();

        final var savedUser = userRepo.save(user);

        final var category = Category.builder()
                .user(savedUser)
                .build();

        final var savedCategory = categoryRepo.save(category);

        var userId = savedUser.getId();
        //var userId = UUID.randomUUID().toString();
        var categoryId = savedCategory.getId();

        assertTrue("Category should exist for the given user and category ID",
                categoryRepo.existsByUserIdAndCategoryId(userId, categoryId));
    }

}
