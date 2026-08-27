package com.project.expensetracker.service.auth;

import com.project.expensetracker.config.JwtProp;
import com.project.expensetracker.dto.AuthResponse;
import com.project.expensetracker.dto.LoginRequestDto;
import com.project.expensetracker.entity.RefreshToken;
import com.project.expensetracker.repo.RefreshTokenRepo;
import com.project.expensetracker.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UsernameAndPasswordAuthService implements AuthService{

    private final AuthenticationManager authenticationManager;
    private final SecretKey secretKey;
    private final JwtProp jwtProp;
    private final RefreshTokenRepo refreshTokenRepo;

    @Override
    public AuthResponse loginUser(LoginRequestDto request) {

        UsernamePasswordAuthenticationToken unauthenticated = new UsernamePasswordAuthenticationToken(request.email(), request.password());

        Authentication authenticated = authenticationManager.authenticate(unauthenticated);

        String email = authenticated.getName();

        Collection<? extends GrantedAuthority> authorities = authenticated.getAuthorities();

        final var accessTokenExpirationMs = jwtProp.getExpirationTimeAccessTime();
        final var accessToken = JwtUtils.generateAccessToken(email, authorities, secretKey, accessTokenExpirationMs);

        long expirationTimeRefreshTime = jwtProp.getExpirationTimeRefreshTime();
        final var refreshToken = JwtUtils.generateRefreshToken(email, secretKey, expirationTimeRefreshTime);
        refreshTokenRepo.save(RefreshToken.builder()
                        .id(Base64.getEncoder().encodeToString(email.getBytes()))
                .token(refreshToken)
                .expirationTime(expirationTimeRefreshTime)
                .build());

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                accessTokenExpirationMs
        );
    }
}
