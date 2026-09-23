package com.project.expensetracker.controller;

import com.project.expensetracker.dto.AiInputDto;
import com.project.expensetracker.dto.AiInsightDto;
import com.project.expensetracker.dto.AiTaskDto;
import com.project.expensetracker.dto.TransactionRequestDto;
import com.project.expensetracker.repo.UserRepo;
import com.project.expensetracker.service.ai.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai-input")
public class AiController {

    private final AiService aiService;
    private final UserRepo userRepo;

    @PostMapping
    ResponseEntity<AiTaskDto> parseRawText(@RequestBody AiInputDto text, Authentication authentication) {
        String userId = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"))
                .getId();
        AiTaskDto response = aiService.save(text, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/latest-insight")
    ResponseEntity<AiInsightDto> getLatestInsight(@AuthenticationPrincipal String userId){
        AiInsightDto latestInsight = aiService.getLatestInsight(userId);
        return ResponseEntity.ok(latestInsight);
    }

    @PostMapping("/generate-insight")
    ResponseEntity<AiInsightDto> generateInsight(@AuthenticationPrincipal String userEmail) {
        return ResponseEntity.ok(aiService.generateAiInsightTask(userEmail));
    }

}
