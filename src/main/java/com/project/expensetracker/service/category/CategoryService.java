package com.project.expensetracker.service.category;

import com.project.expensetracker.entity.Category;

import java.util.List;

public interface CategoryService {
    boolean existByUserAndCategory(String userId, Long categoryId);

    List<Category> getAllWithoutUserId();

    Category getByName(String categoryName);
}
