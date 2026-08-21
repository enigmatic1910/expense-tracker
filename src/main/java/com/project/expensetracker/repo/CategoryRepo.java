package com.project.expensetracker.repo;

import com.project.expensetracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepo extends JpaRepository<Category,Long> {

    @Query("select count(c) > 0 from Category c join c.user u where u.id = :userId and c.id = :categoryId")
    boolean existsByUserIdAndCategoryId(String userId, Long categoryId);
}
