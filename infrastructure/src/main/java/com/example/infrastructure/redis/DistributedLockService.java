package com.example.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DistributedLockService {

    private final RedisTemplate<String, String> redisTemplate;
    /**
     * Atomically releases the lock only if the current
     * lock value belongs to the caller.
     */
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT =
            new DefaultRedisScript<>(
                    """
                    if redis.call('get', KEYS[1]) == ARGV[1] then
                        return redis.call('del', KEYS[1])
                    else
                        return 0
                    end
                    """,
                    Long.class
            );
    /**
     * Attempts to acquire a distributed Redis lock.
     * @param key lock key
     * @param ttl maximum time the lock can remain held
     * @return unique lock value when acquired, otherwise null
     */
    public String tryLock(
            String key,
            Duration ttl
    ) {
        String lockValue =
                UUID.randomUUID().toString();
        Boolean acquired =
                redisTemplate.opsForValue().setIfAbsent(
                        key,
                        lockValue,
                        ttl
                );
        return Boolean.TRUE.equals(acquired)
                ? lockValue
                : null;
    }
    /**
     * Releases the lock only when the supplied lock value
     * matches the value stored in Redis.
     */
    public void unlock(
            String lockKey,
            String lockValue
    ) {
        if (lockValue == null) {
            return;
        }
        redisTemplate.execute(
                UNLOCK_SCRIPT,
                Collections.singletonList(lockKey),
                lockValue
        );
    }
}