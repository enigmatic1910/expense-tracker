package com.project.expensetracker.repo;

import com.project.expensetracker.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepo extends JpaRepository<Category,Long> {

    @Query("select count(c) > 0 from Category c left join c.user u where c.id = :categoryId and (u.id = :userId or u is null)")
    boolean existsByUserIdAndCategoryId(String userId, Long categoryId);

    List<Category> findAllByUserIsNull();

    @Query("select c from Category c where c.user.id = :userId or c.user is null")
    List<Category> findAllForUser(String userId);

    @Query("select c from Category c where c.user is null")
    List<Category> getAllSystemCategory();

    Category findByName(String categoryName);

    @Query("select c from Category c where lower(c.name) = lower(:name) and (c.user.id = :userId or c.user is null)")
    Category findByNameForUser(String name, String userId);
}
