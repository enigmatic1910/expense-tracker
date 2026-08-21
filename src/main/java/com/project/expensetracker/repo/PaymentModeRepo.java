package com.project.expensetracker.repo;

import com.project.expensetracker.entity.PaymentMode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentModeRepo extends JpaRepository<PaymentMode, Long> {

}
