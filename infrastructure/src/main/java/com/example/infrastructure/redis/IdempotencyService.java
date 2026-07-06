package com.example.infrastructure.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
public class IdempotencyService {
    private static final String PREFIX = "processed-event:";
    private final StringRedisTemplate redisTemplate;
    public IdempotencyService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    /**
     * Returns true if this event has already been processed.
     */
    public boolean isProcessed(UUID eventId) {
        return isProcessed("default", eventId);
    }

    public boolean isProcessed(String consumerName, UUID eventId) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(key(consumerName, eventId))
        );
    }
    /**
     * Marks an event as processed.
     */
    public void markProcessed(UUID eventId) {
        markProcessed("default", eventId);
    }

    public void markProcessed(String consumerName, UUID eventId) {
        redisTemplate.opsForValue().set(
                key(consumerName, eventId),
                "processed",
                Duration.ofHours(24)
        );
    }

    private String key(String consumerName, UUID eventId) {
        return PREFIX + consumerName + ":" + eventId;
    }
}
