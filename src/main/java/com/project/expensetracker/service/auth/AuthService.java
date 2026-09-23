package com.project.expensetracker.service.auth;

import com.project.expensetracker.dto.AuthResponse;
import com.project.expensetracker.dto.LoginRequestDto;
import com.project.expensetracker.dto.TokenRefreshRequest;

public interface AuthService {
    AuthResponse loginUser(LoginRequestDto request);

    AuthResponse refreshToken(TokenRefreshRequest request);
}
