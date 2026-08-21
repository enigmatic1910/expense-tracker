package com.project.expensetracker.repo;

import com.project.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
}
