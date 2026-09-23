package com.project.expensetracker.repo;

import com.project.expensetracker.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BankRepo extends JpaRepository<Bank, UUID> {
    Optional<Bank> findByName(String bankName);
}
