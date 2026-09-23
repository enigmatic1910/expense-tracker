package com.project.expensetracker.service.card;

import com.project.expensetracker.dto.CardDto;
import com.project.expensetracker.dto.CardSummaryDto;
import com.project.expensetracker.dto.UserCards;
import com.project.expensetracker.entity.Account;
import com.project.expensetracker.entity.Card;
import com.project.expensetracker.entity.User;
import com.project.expensetracker.enums.AccountType;
import com.project.expensetracker.enums.CardType;
import com.project.expensetracker.repo.AccountRepo;
import com.project.expensetracker.repo.CardRepo;
import com.project.expensetracker.repo.UserRepo;
import com.project.expensetracker.repo.TransactionRepo;
import com.project.expensetracker.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepo cardRepo;
    private final UserRepo userRepo;
    private final AccountService accountService;
    private final AccountRepo accountRepo;
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
            Account account;

            if(cardDto.cardType() == CardType.CREDIT_CARD){
                account = accountRepo.save(Account.builder()
                                .user(user)
                                .accountType(AccountType.CREDIT)
                                .balance(cardDto.limit() != null ? cardDto.limit() : 0.0)
                                .lastFourDigits(cardDto.lastFourDigits())
                                .bank(cardDto.bank() != null ? cardDto.bank() : null)
                        .build());
            }
            else{
                account = accountService.getAccount(userId, cardDto.accountId());
            }

            return Card.builder()
                    .user(user)
                    .account(account)
                    .cardType(cardDto.cardType())
                    .lastFourDigits(cardDto.lastFourDigits())
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
                card.getAccount() != null ? card.getAccount().getId() : null,
                card.getCardType() == CardType.CREDIT_CARD &&
                        card.getCreditLimit() != null
                        ? card.getCreditLimit()
                        : null,
                card.getAccount() != null ? card.getAccount().getBank() : null
        );
    }
}
