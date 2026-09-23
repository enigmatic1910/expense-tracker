package com.project.expensetracker.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/text")
public class TextController {

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    ResponseEntity<?> getSecretText(@AuthenticationPrincipal String username){
        final var response = "Cuurently logged in as %s".formatted(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/anonymous")
    ResponseEntity<?> getSecretTextForGuest(@AuthenticationPrincipal String username){
        final var response = "Cuurently logged in as %s".formatted(username);
        return ResponseEntity.ok(response);
    }
}
