package com.project.expensetracker.service.auth;

import com.project.expensetracker.config.JwtProp;
import com.project.expensetracker.dto.AuthResponse;
import com.project.expensetracker.dto.LoginRequestDto;
import com.project.expensetracker.entity.User;
import com.project.expensetracker.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UsernameAndPasswordAuthService implements AuthService{

    private final AuthenticationManager authenticationManager;
    private final SecretKey secretKey;
    private final JwtProp jwtProp;

    @Override
    public AuthResponse loginUser(LoginRequestDto request) {

        UsernamePasswordAuthenticationToken unauthenticated = new UsernamePasswordAuthenticationToken(request.email(), request.password());

        Authentication authenticated = authenticationManager.authenticate(unauthenticated);

        String email = (authenticated.getPrincipal() instanceof User user) ? user.getEmail() : "";

        Collection<? extends GrantedAuthority> authorities = authenticated.getAuthorities();

        final var accessTokenExpirationMs = jwtProp.getExpirationTimeAccessTime();
        final var accessToken = JwtUtils.generateAccessToken(email, authorities, secretKey, accessTokenExpirationMs);

        return new AuthResponse(
                accessToken,
                null,
                "Bearer",
                accessTokenExpirationMs
        );
    }
}
