package com.project.expensetracker.service.onboarding;

import com.project.expensetracker.dto.OnboardingRequestDto;

public interface OnboardingService {
    void onBoard(OnboardingRequestDto request, String userId);
}
