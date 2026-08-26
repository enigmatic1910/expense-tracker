package com.project.expensetracker.service.category;

import com.project.expensetracker.entity.Category;
import com.project.expensetracker.repo.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepo categoryRepo;

    @Override
    public boolean existByUserAndCategory(String userId, Long categoryId) {

        return categoryRepo.existsByUserIdAndCategoryId(userId, categoryId);
    }

    @Override
    public List<Category> getAllWithoutUserId() {
        return categoryRepo.findAllByUserIsNull();
    }

    @Override
    public Category getByName(String categoryName) {
        return categoryRepo.findByName(categoryName);
    }
}
