package com.project.expensetracker.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="redis")
@Getter
@Setter
public class RedisProps {
    String host;
    int port;
}
