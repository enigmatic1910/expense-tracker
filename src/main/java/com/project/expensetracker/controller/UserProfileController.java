package com.project.expensetracker.controller;

import com.project.expensetracker.dto.UserProfileDto;
import com.project.expensetracker.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserRepo userRepo;

    @GetMapping("/me")
    ResponseEntity<UserProfileDto> getCurrentUser(Authentication authentication) {
        final var user = userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

        return ResponseEntity.ok(new UserProfileDto(user.getName()));
    }
}
