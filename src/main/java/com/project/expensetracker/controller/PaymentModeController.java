package com.project.expensetracker.controller;

import com.project.expensetracker.entity.PaymentMode;
import com.project.expensetracker.repo.PaymentModeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping ("/api/payment-modes")
public class PaymentModeController {

    public final PaymentModeRepo paymentModeRepo;

    @GetMapping("/all")
    public ResponseEntity<List<PaymentMode>> getAllPaymentModes() {
        List<PaymentMode> paymentModes = paymentModeRepo.findAll();
        return ResponseEntity.ok(paymentModes);
    }
}
