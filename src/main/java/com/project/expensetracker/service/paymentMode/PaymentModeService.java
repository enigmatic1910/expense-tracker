package com.project.expensetracker.service.paymentMode;

import com.project.expensetracker.entity.PaymentMode;

public interface PaymentModeService {
    boolean existsById(Long id);

    PaymentMode get(Long paymentedModeId);
}
