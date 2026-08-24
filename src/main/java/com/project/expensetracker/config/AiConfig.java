package com.project.expensetracker.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class AiConfig {

    @Bean
    ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultOptions(
                        ChatOptions.builder()
                                .model("gemma3:4b")
                                .temperature(0.2)
                )
                .build();
    }

    @Bean
    AtomicInteger requestCounter(){
        return new AtomicInteger(0);
    }
}
