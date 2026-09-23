package com.project.expensetracker.service.category;

import com.project.expensetracker.entity.Category;
import com.project.expensetracker.entity.User;
import com.project.expensetracker.dto.SystemCategoryDto;
import com.project.expensetracker.repo.CategoryRepo;
import com.project.expensetracker.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;
    private final UserRepo userRepo;

    @Override
    public boolean existByUserAndCategory(String userId, Long categoryId) {
        return categoryRepo.existsByUserIdAndCategoryId(userId, categoryId);
    }

    @Override
    public List<Category> getAllWithoutUserId() {
        return categoryRepo.findAllByUserIsNull();
    }

    @Override
    public List<Category> getAllForUser(String userId) {
        return categoryRepo.findAllForUser(userId);
    }

    @Override
    public List<SystemCategoryDto> getAllSystemCategory() {
        return categoryRepo.getAllSystemCategory()
                .stream()
                .map(category -> new SystemCategoryDto(category.getId(), category.getName()))
                .toList();
    }

    @Override
    public Category getByName(String categoryName) {
        return categoryRepo.findByName(categoryName);
    }

    @Override
    public Category getByNameForUser(String categoryName, String userId) {
        Category category = categoryRepo.findByNameForUser(categoryName, userId);
        if (category == null) {
            throw new IllegalArgumentException("Category not found for user: " + categoryName);
        }
        return category;
    }

    @Override
    public SystemCategoryDto saveCategory(String categoryName, String userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Category category = Category.builder()
                .name(categoryName)
                .user(user)
                .build();
        
        Category savedCategory = categoryRepo.save(category);
        return new SystemCategoryDto(savedCategory.getId(), savedCategory.getName());
    }
}
