package com.project.expensetracker.service.paymentMode;

import com.project.expensetracker.entity.PaymentMode;
import com.project.expensetracker.exception.PaymentModeNotFoundException;
import com.project.expensetracker.repo.PaymentModeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentModeServiceImpl implements PaymentModeService {

    private final PaymentModeRepo paymentModeRepo;

    @Override
    public boolean existsById(Long id) {
        return paymentModeRepo.existsById(id);
    }

    @Override
    public PaymentMode get(Long paymentModeId) {
        return paymentModeRepo.findById(paymentModeId)
                .orElseThrow(() -> new PaymentModeNotFoundException(paymentModeId));
    }
}
