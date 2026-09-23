package com.project.expensetracker.service.auth;

import com.project.expensetracker.config.JwtProp;
import com.project.expensetracker.dto.AuthResponse;
import com.project.expensetracker.dto.LoginRequestDto;
import com.project.expensetracker.dto.TokenRefreshRequest;
import com.project.expensetracker.entity.RefreshToken;
import com.project.expensetracker.entity.User;
import com.project.expensetracker.exception.InvalidRefreshTokenException;
import com.project.expensetracker.repo.RefreshTokenRepo;
import com.project.expensetracker.security.BearerAuthToken;
import com.project.expensetracker.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

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

        return getAuthResponse(authenticated);
    }

    private @NonNull AuthResponse getAuthResponse(Authentication authenticated) {
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

    @Override
    public AuthResponse refreshToken(TokenRefreshRequest request) {

        final var accessToken = request.accessToken();
        final var refreshToken = request.refreshToken();

        final var claims = JwtUtils.getClaimsFromToken(accessToken, secretKey);

        final var username = claims.get(Claims.SUBJECT, String.class);

        final var refreshTokenClaims = JwtUtils.getClaimsFromToken(refreshToken, secretKey);
        final var refreshTokenUsername = refreshTokenClaims.get(Claims.SUBJECT, String.class);

        if(!username.equals(refreshTokenUsername)){
            refreshTokenRepo.deleteById(username);
            throw new InvalidRefreshTokenException("Refresh token does not match the access token");
        }

        final var existingToken = refreshTokenRepo.findById(Base64.getEncoder()
                .encodeToString(username.getBytes()))
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token not found"));

        if(!existingToken.getToken().equals(refreshToken)){
            refreshTokenRepo.delete(existingToken);
            throw new InvalidRefreshTokenException("Refresh token does not match the access token");
        }

        refreshTokenRepo.delete(existingToken);

        return getAuthResponse(BearerAuthToken.authenticated(username, JwtUtils.getAuthorities(claims)));

    }
}
