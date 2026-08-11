package com.example.infrastructure.autoconfigure;

import com.example.infrastructure.redis.DistributedLockService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

@AutoConfiguration
public class RedisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DistributedLockService distributedLockService(
            RedisTemplate<String, String> redisTemplate
    ) {
        return new DistributedLockService(redisTemplate);
    }
}