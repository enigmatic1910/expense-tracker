package com.project.expensetracker.utils;

import io.jsonwebtoken.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.www.NonceExpiredException;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;


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

    static Claims parseToken(String s, SecretKey secretKey) {
        JwtParser parser = Jwts.parser()
                .verifyWith(secretKey)
                .build();

        try{
           return parser.parseSignedClaims(s)
                   .getPayload();
        }
        catch (ExpiredJwtException ex){
            throw new NonceExpiredException(ex.getMessage());
        }
        catch(JwtException | IllegalArgumentException e){
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    static List<? extends GrantedAuthority> getAuthorities(Claims claims) {
        return claims.get("roles", List.class) instanceof List<?> list ? list.stream()
                .map(role -> new SimpleGrantedAuthority(role.toString()))
                .toList()
                : Collections.emptyList();
    }

    static Claims getClaimsFromToken(String accessToken, SecretKey secretKey) {

        JwtParser parser = Jwts.parser()
                .verifyWith(secretKey)
                .build();

        try{
            return parser.parseSignedClaims(accessToken)
                    .getPayload();
        }
        catch (ExpiredJwtException ex){
            return ex.getClaims();
        }
        catch(JwtException | IllegalArgumentException e){
            throw new RuntimeException("Invalid JWT token", e);
        }

    }
}
