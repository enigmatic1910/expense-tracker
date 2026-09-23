package com.project.expensetracker.controller;

import com.project.expensetracker.dto.UserConfigDto;
import com.project.expensetracker.service.userConfig.UserConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-config")
@RequiredArgsConstructor
public class UserConfigController {

    private final UserConfigService userConfigService;

    @GetMapping
    ResponseEntity<UserConfigDto> getUserConfig(@AuthenticationPrincipal String userId) {
        final var userConfig = userConfigService.getConfig(userId);
        return ResponseEntity.ok(userConfig);
    }

    @PostMapping("/update")
    ResponseEntity<UserConfigDto> updateUserConfig(@RequestBody UserConfigDto userConfigDto, @AuthenticationPrincipal String userId) {
        final var updatedUserConfig = userConfigService.updateConfig(userConfigDto, userId);

        return ResponseEntity.ok(updatedUserConfig);
    }
}
