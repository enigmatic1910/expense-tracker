package com.project.expensetracker.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JwtProp {
    String secretKey;
    long expirationTimeAccessTime;
}
