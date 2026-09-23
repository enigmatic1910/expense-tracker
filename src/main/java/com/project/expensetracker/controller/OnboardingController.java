package com.project.expensetracker.controller;

import com.project.expensetracker.dto.OnboardingRequestDto;
import com.project.expensetracker.service.onboarding.OnboardingService;
import com.project.expensetracker.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;

    @PostMapping("/onboarding")
    public ResponseEntity<Void> onboardUser(@RequestBody OnboardingRequestDto request, @AuthenticationPrincipal String userId) {
        onboardingService.onBoard(request, userId);
        return ResponseEntity.ok().build();
    }
}

