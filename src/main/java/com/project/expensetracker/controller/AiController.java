package com.project.expensetracker.controller;

import com.project.expensetracker.dto.AiInputDto;
import com.project.expensetracker.dto.AiTaskDto;
import com.project.expensetracker.dto.TransactionRequestDto;
import com.project.expensetracker.service.ai.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai-input")
public class AiController {

    private final String LOGGED_IN_USER = "674a1664-ce08-47fc-a055-22c109216b78";
    private final AiService aiService;

    @PostMapping
    ResponseEntity<AiTaskDto> parseRawText(@RequestBody AiInputDto text){
        AiTaskDto response = aiService.save(text, LOGGED_IN_USER);
        return ResponseEntity.ok(response);
    }

}
