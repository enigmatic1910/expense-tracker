package com.project.expensetracker.repo;

import com.project.expensetracker.entity.UserConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserConfigRepo extends JpaRepository<UserConfig, Long> {

    @Query("select c from UserConfig c where c.user.id = :id")
    Optional<UserConfig> findByUserId(String id);
}
