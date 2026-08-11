package com.example.eventstream.gateway.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RateLimitProperties.class)
public class RateLimitConfiguration {
    @Bean
    public RedisRateLimiter redisRateLimiter(
            RateLimitProperties rateLimitProperties) {
        return new RedisRateLimiter(
                rateLimitProperties.getReplenishRate(),
                rateLimitProperties.getBurstCapacity(),
                rateLimitProperties.getRequestedTokens()
        );
    }
}