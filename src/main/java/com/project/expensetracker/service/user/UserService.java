package com.project.expensetracker.service.user;

import com.project.expensetracker.dto.RegisterRequestDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    void registerUser(RegisterRequestDto request);
}
