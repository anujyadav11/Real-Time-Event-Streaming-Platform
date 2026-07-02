package com.example.infrastructure.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class IdempotencyService {
    private static final String PREFIX = "processed-event";
    private final StringRedisTemplate redisTemplate;
    public IdempotencyService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    /**
     * Returns true if this event has already been processed.
     */
    public boolean isProcessed(UUID eventId) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(PREFIX + eventId)
        );
    }
    /**
     * Marks an event as processed.
     */
    public void markProcessed(UUID eventId) {
        redisTemplate.opsForValue().set(
                PREFIX + eventId,
                "processed",
                Duration.ofHours(24)
        );
    }
}
