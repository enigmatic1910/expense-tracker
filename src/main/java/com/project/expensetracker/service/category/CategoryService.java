package com.project.expensetracker.service.category;

public interface CategoryService {
    boolean existByUserAndCategory(String userId, Long categoryId);
}
