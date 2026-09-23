package com.project.expensetracker.security;


import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/api/auth/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String requestHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(requestHeader == null || requestHeader.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        final var token = extractToken(requestHeader);

        if(token.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        final var unauthenticatedToken = BearerAuthToken.unauthenticated(token.get());
        try {
            Authentication authenticated = authenticationManager.authenticate(unauthenticatedToken);

            SecurityContextHolder.getContext().setAuthentication(authenticated);

        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
        } catch (RuntimeException e) {
            // Invalid or expired JWTs must become an unauthenticated request so
            // the client can use its refresh token and retry the request.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private Optional<String> extractToken(String requestHeader) {
        if(StringUtils.isBlank(requestHeader) || !requestHeader.startsWith("Bearer ")){
            return Optional.empty();
        }
        return Optional.of(requestHeader.substring(7));
    }
}
