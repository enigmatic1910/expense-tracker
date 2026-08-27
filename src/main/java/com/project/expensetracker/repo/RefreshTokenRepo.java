package com.project.expensetracker.repo;

import com.project.expensetracker.entity.RefreshToken;

import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepo extends CrudRepository<RefreshToken, String> {
}
