package com.project.expensetracker.service.category;

import com.project.expensetracker.repo.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;

    @Override
    public boolean existByUserAndCategory(String userId, Long categoryId) {

        return categoryRepo.existsByUserIdAndCategoryId(userId, categoryId);
    }
}
