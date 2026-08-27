package com.project.expensetracker.service.auth;

import com.project.expensetracker.dto.AuthResponse;
import com.project.expensetracker.dto.LoginRequestDto;

public interface AuthService {
    AuthResponse loginUser(LoginRequestDto request);
}
