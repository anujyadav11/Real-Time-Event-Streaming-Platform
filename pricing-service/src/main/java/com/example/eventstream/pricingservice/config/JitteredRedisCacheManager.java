package com.example.eventstream.pricingservice.config;

import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.time.Duration;
import java.util.Random;

public class JitteredRedisCacheManager
        extends RedisCacheManager {

    private final Duration baseTtl;
    private final Duration jitter;

    private final Random random = new Random();
    public JitteredRedisCacheManager(
            RedisCacheWriter cacheWriter,
            RedisCacheConfiguration defaultCacheConfiguration,
            Duration baseTtl,
            Duration jitter
    ) {
        super(
                cacheWriter,
                defaultCacheConfiguration
        );
        this.baseTtl = baseTtl;
        this.jitter = jitter;
    }
    /**
     * Returns a TTL between:
     * baseTtl - jitter
     * and
     * baseTtl + jitter
     */
    private Duration randomTtl() {
        long baseMillis = baseTtl.toMillis();
        long jitterMillis = jitter.toMillis();
        if (jitterMillis <= 0) {
            return baseTtl;
        }
        long min = Math.max(1,baseMillis - jitterMillis);
        long max = baseMillis + jitterMillis;
        long ttl = min + (long) (random.nextDouble() * (max - min));
        return Duration.ofMillis(ttl);
    }
}