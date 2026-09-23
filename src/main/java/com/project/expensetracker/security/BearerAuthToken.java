package com.project.expensetracker.security;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class BearerAuthToken extends AbstractAuthenticationToken {

    private static final String TOKEN_TYPE = "Bearer";
    private final String username;
    private final String credentials;

    public BearerAuthToken(@Nullable Collection<? extends GrantedAuthority> authorities, String username, String credentials, boolean isAuthenticated) {
        super(authorities);
        this.username = username;
        this.credentials = credentials;
        this.setAuthenticated(isAuthenticated);
    }

    public static BearerAuthToken unauthenticated(String token){
        return new BearerAuthToken(null, null, token, false);
    }

    public static BearerAuthToken authenticated(String username, Collection<? extends GrantedAuthority> authorities){
        return new BearerAuthToken(authorities, username, null, true);

    }

    @Override
    public @Nullable Object getCredentials() {
        return credentials;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return username;
    }
}
