package com.project.expensetracker.service.card;

import com.project.expensetracker.dto.CardDto;
import com.project.expensetracker.dto.CardSummaryDto;
import com.project.expensetracker.dto.UserCards;
import com.project.expensetracker.entity.Card;
import com.project.expensetracker.entity.User;
import com.project.expensetracker.enums.CardType;
import com.project.expensetracker.repo.CardRepo;
import com.project.expensetracker.repo.UserRepo;
import com.project.expensetracker.repo.TransactionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepo cardRepo;
    private final UserRepo userRepo;
    private final TransactionRepo transactionRepo;

    @Override
    public List<Card> getAllCardsByUserId(String userId) {
        return cardRepo.findByUser(userId);
    }

    @Override
    public List<CardDto> getAllCardDtosByUserId(String userId) {
        return cardRepo.findByUser(userId)
                .stream()
                .map(toDto())
                .toList();
    }

    @Override
    public List<CardDto> addCards(String userId, UserCards userCards) {
        final var user = userRepo.findByEmail(userId).orElseThrow(() -> new RuntimeException("User not found"));

        if(userCards == null || userCards.cards() == null || userCards.cards().isEmpty()){
            return List.of();
        }

        final var cardsToSave = userCards.cards().stream().map(cardDto -> {
            if (cardDto == null || cardDto.lastFourDigits() == null
                    || !cardDto.lastFourDigits().trim().matches("\\d{4}")) {
                throw new IllegalArgumentException("Last four digits must be exactly 4 numbers");
            }

            return Card.builder()
                    .user(user)
                    .cardType(cardDto.cardType())
                    .lastFourDigits(cardDto.lastFourDigits().trim())
                    .creditLimit(cardDto.limit() != null ? cardDto.limit() : 0L)
                    .build();
        })
                .toList();

        return cardRepo.saveAll(cardsToSave)
                .stream()
                .map(toDto())
                .toList();

    }

    @Transactional
    @Override
    public void deleteCard(String userId, String cardId) {
        final Card card = cardRepo.findByIdAndUserEmail(cardId, userId);

        if (card == null) {
            throw new RuntimeException("Card not found");
        }

        card.setActive(false);
        cardRepo.save(card);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CardSummaryDto> getCardSummaries(String userId) {
        return cardRepo.findByUser(userId).stream()
                .map(card -> new CardSummaryDto(
                        card.getId(),
                        card.getCardType(),
                        card.getLastFourDigits(),
                        card.getCreditLimit(),
                        transactionRepo.sumExpenseByCardId(userId, card.getId())
                ))
                .toList();
    }

    private static Function<Card, CardDto> toDto() {
        return card -> new CardDto(
                card.getId(),
                card.getCardType(),
                card.getLastFourDigits(),
                null,
                card.getCardType() == CardType.CREDIT_CARD &&
                        card.getCreditLimit() != null
                        ? card.getCreditLimit()
                        : null,
                null
        );
    }
}
