package com.project.expensetracker.security;

import com.project.expensetracker.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.List;

@Component("bearerAuthTokenProvider")
@RequiredArgsConstructor
public class BearerAuthTokenProvider implements AuthenticationProvider {

    private final SecretKey secretKey;
    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {

        Claims claims = JwtUtils.parseToken((authentication instanceof BearerAuthToken authToken) ? (String) authentication.getCredentials() : "", secretKey);

        final var username = claims.get(Claims.SUBJECT, String.class);

        final List<? extends GrantedAuthority> roles = JwtUtils.getAuthorities(claims);

        return BearerAuthToken.authenticated(username, roles);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(BearerAuthToken.class);
    }
}
