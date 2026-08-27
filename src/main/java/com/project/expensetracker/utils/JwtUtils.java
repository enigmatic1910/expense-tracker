package com.project.expensetracker.utils;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;


public interface JwtUtils {

    static String generateAccessToken(String username, Collection<? extends GrantedAuthority> roles, SecretKey secretKey, long accessTokenExpirationMs) {
        return generateToken(username, roles, secretKey, accessTokenExpirationMs, false);
    }

    private static String generateToken(String username, Collection<? extends GrantedAuthority> roles, SecretKey secretKey, long accessTokenExpirationMs, boolean isRefreshToken) {

        JwtBuilder jwtBuilder = Jwts.builder();

        final var claim = Jwts.claims()
                .subject(username)
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(accessTokenExpirationMs)))
                .add("roles", (isRefreshToken || roles == null) ? Collections.emptyList() : roles.stream().map(GrantedAuthority::getAuthority).toList())
                .build();

        jwtBuilder
                .claims(claim)
                .signWith(secretKey);
        return jwtBuilder.compact();
    }

    static String generateRefreshToken(String email, SecretKey secretKey, long expirationTimeRefreshTime) {
        return generateToken(email, null, secretKey, expirationTimeRefreshTime, true);
    }
}
