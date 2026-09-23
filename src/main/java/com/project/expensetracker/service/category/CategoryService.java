package com.project.expensetracker.service.category;

import com.project.expensetracker.entity.Category;
import com.project.expensetracker.dto.SystemCategoryDto;

import java.util.List;

public interface CategoryService {
    boolean existByUserAndCategory(String userId, Long categoryId);

    List<Category> getAllWithoutUserId();

    List<Category> getAllForUser(String userId);

    List<SystemCategoryDto> getAllSystemCategory();

    Category getByName(String categoryName);

    Category getByNameForUser(String categoryName, String userId);

    SystemCategoryDto saveCategory(String categoryName, String userId);
}
