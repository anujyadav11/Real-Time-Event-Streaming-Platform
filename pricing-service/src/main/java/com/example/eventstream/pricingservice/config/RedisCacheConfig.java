package com.example.eventstream.pricingservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisCacheConfig {
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(
            @Value("${pricing.cache.ttl:10m}") Duration cacheTtl
    ) {
        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer();
        return RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(cacheTtl)
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(serializer)
                );
    }
    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            RedisCacheConfiguration redisCacheConfiguration,
            @Value("${pricing.cache.missing-ttl:30s}")
            Duration missingCacheTtl
    ) {
        RedisCacheConfiguration missingCacheConfiguration =
                redisCacheConfiguration
                        .entryTtl(missingCacheTtl);
        return RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .withCacheConfiguration(
                        "product-prices-missing",
                        missingCacheConfiguration
                )
                .build();
    }
}