package com.project.expensetracker.service.card;

import com.project.expensetracker.dto.CardDto;
import com.project.expensetracker.dto.CardSummaryDto;
import com.project.expensetracker.dto.UserCards;
import com.project.expensetracker.entity.Card;

import java.util.List;

public interface CardService {

    List<Card> getAllCardsByUserId(String userId);

    List<CardDto> getAllCardDtosByUserId(String userId);

    List<CardDto> addCards(String userId, UserCards userCards);

    void deleteCard(String userId, String cardId);

    List<CardSummaryDto> getCardSummaries(String userId);
}
