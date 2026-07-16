package com.example.eventstream.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class UserKeyResolverConfig {
    @Bean
    public KeyResolver userKeyResolver(){
        return exchange -> {
            String username = exchange.getAttribute("username");
            if(username == null || username.isBlank()){
                username = "anonymous";
            }
        return Mono.just(username);
        };
    }
}
