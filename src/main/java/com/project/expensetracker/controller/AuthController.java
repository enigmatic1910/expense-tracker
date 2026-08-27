package com.project.expensetracker.controller;

import com.project.expensetracker.dto.LoginRequestDto;
import com.project.expensetracker.dto.RegisterRequestDto;
import com.project.expensetracker.service.auth.AuthService;
import com.project.expensetracker.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping(value="/register")
    ResponseEntity<?> register(@RequestBody RegisterRequestDto request){

        userService.registerUser(request);
        return null;
    }

    @PostMapping(value = "/login")
    ResponseEntity<?> login(@RequestBody LoginRequestDto request){
        var authResponse = authService.loginUser(request);
        return ResponseEntity.ok(authResponse);
    }
}
