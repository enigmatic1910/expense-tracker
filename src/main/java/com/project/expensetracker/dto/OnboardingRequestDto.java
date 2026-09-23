package com.project.expensetracker.dto;

import com.project.expensetracker.enums.CardType;
import com.project.expensetracker.enums.LanguagePreference;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


public record OnboardingRequestDto(
        UUID bankId,
        String accountLastFourDigits,
        Double balance,
        CardType cardType,
        String cardLastFourDigits,
        Double cashBalance,
        Long paymentModeId,
        LanguagePreference languagePreference,
        Long cardLimit
) {
}

