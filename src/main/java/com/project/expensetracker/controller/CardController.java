package com.project.expensetracker.controller;

import com.project.expensetracker.dto.CardDto;
import com.project.expensetracker.dto.CardSummaryDto;
import com.project.expensetracker.dto.UserCards;
import com.project.expensetracker.service.card.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping
    ResponseEntity<List<CardDto>> getCards(@AuthenticationPrincipal String userId){
        return ResponseEntity.ok(cardService.getAllCardDtosByUserId(userId));
    }

    @PostMapping("/add")
    ResponseEntity<List<CardDto>> addCard(@AuthenticationPrincipal String userId, @RequestBody UserCards userCards){

        return ResponseEntity.ok(cardService.addCards(userId, userCards));
    }

    @GetMapping("/summary")
    ResponseEntity<List<CardSummaryDto>> getCardSummaries(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(cardService.getCardSummaries(userId));
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<?> deleteCard(@AuthenticationPrincipal String userId, @PathVariable String cardId){
        cardService.deleteCard(userId, cardId);
        return ResponseEntity.ok().build();
    }

}
