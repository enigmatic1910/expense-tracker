package com.project.expensetracker.repo;

import com.project.expensetracker.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BankRepo extends JpaRepository<Bank, Long> {
    Optional<Bank> findByName(String bankName);
}
