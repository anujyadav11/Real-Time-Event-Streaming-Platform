package com.example.eventstream.pricingservice.service;

import com.example.eventstream.common.dto.ProductPriceResponse;
import com.example.eventstream.pricingservice.exception.ProductNotFoundException;
import com.example.infrastructure.redis.DistributedLockService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

@Service
public class PricingService {
    private static final Logger log =
            LoggerFactory.getLogger(PricingService.class);
    private static final String CACHE_NAME =
            "product-prices";
    private static final String LOCK_KEY_PREFIX =
            "lock:pricing:";
    private static final Map<Long, BigDecimal> PRODUCT_PRICES = Map.of(
            1L, BigDecimal.valueOf(299.99),
            2L, BigDecimal.valueOf(149.50),
            3L, BigDecimal.valueOf(799.00),
            4L, BigDecimal.valueOf(59.99)
    );
    private final CacheManager cacheManager;
    private final DistributedLockService distributedLockService;

    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;
    private final Counter productNotFoundCounter;
    private final Counter lockAcquiredCounter;
    private final Counter lockWaitCounter;
    private final Duration lockTtl;
    private final Duration retryDelay;
    private final int maxRetries;
    public PricingService(
            CacheManager cacheManager,
            DistributedLockService distributedLockService,
            MeterRegistry meterRegistry,
            @Value("${pricing.cache.lock.ttl:5s}")
            Duration lockTtl,
            @Value("${pricing.cache.lock.retry-delay:50ms}")
            Duration retryDelay,
            @Value("${pricing.cache.lock.max-retries:20}")
            int maxRetries
    ) {
        this.cacheManager = cacheManager;
        this.distributedLockService = distributedLockService;
        this.lockTtl = lockTtl;
        this.retryDelay = retryDelay;
        this.maxRetries = maxRetries;
        this.cacheHitCounter =
                meterRegistry.counter(
                        "pricing.cache.hit"
                );
        this.cacheMissCounter =
                meterRegistry.counter(
                        "pricing.cache.miss"
                );
        this.productNotFoundCounter =
                meterRegistry.counter(
                        "pricing.product.not_found"
                );
        this.lockAcquiredCounter =
                meterRegistry.counter(
                        "pricing.cache.lock.acquired"
                );
        this.lockWaitCounter =
                meterRegistry.counter(
                        "pricing.cache.lock.wait"
                );
    }
    public ProductPriceResponse getPrice(
            Long productId
    ) {
        Cache cache =
                getCache();
        /*
         * First cache check.
         *
         * Most requests should return from here.
         */
        ProductPriceResponse cached =
                cache.get(
                        productId,
                        ProductPriceResponse.class
                );
        if (cached != null) {
            cacheHitCounter.increment();
            log.debug(
                    "Pricing cache HIT for product {}",
                    productId
            );
            return cached;
        }
        cacheMissCounter.increment();
        log.info(
                "Pricing cache MISS for product {}",
                productId
        );
        String lockKey =
                LOCK_KEY_PREFIX + productId;
        /*
         * Try to become the single request responsible
         * for rebuilding the cache.
         */
        String lockValue =
                distributedLockService.tryLock(
                        lockKey,
                        lockTtl
                );
        if (lockValue != null) {
            lockAcquiredCounter.increment();
            try {
                /*
                 * IMPORTANT:
                 *
                 * Another instance may have populated the
                 * cache between our first cache check and
                 * acquiring the lock.
                 *
                 * Therefore we MUST check Redis again.
                 */
                cached =
                        cache.get(
                                productId,
                                ProductPriceResponse.class
                        );
                if (cached != null) {
                    cacheHitCounter.increment();
                    log.debug(
                            "Pricing cache populated while waiting for lock. "
                                    + "productId={}",
                            productId
                    );
                    return cached;
                }
                /*
                 * We are the only instance allowed to
                 * rebuild this cache entry.
                 */
                ProductPriceResponse response =
                        loadPrice(productId);
                cache.put(
                        productId,
                        response
                );
                log.info(
                        "Pricing cache populated for product {}",
                        productId
                );
                return response;
            } finally {
                distributedLockService.unlock(
                        lockKey,
                        lockValue
                );
            }
        }
        /*
         * Another instance is currently rebuilding
         * the cache.
         *
         * Wait briefly and check Redis again instead
         * of hitting the underlying data source.
         */
        lockWaitCounter.increment();
        for (int attempt = 1;
             attempt <= maxRetries;
             attempt++) {
            sleep();
            cached =
                    cache.get(
                            productId,
                            ProductPriceResponse.class
                    );
            if (cached != null) {
                cacheHitCounter.increment();
                log.debug(
                        "Pricing cache HIT after waiting. "
                                + "productId={}, attempt={}",
                        productId,
                        attempt
                );
                return cached;
            }
        }
        /*
         * The lock holder may have failed or taken longer
         * than expected.
         * Do NOT blindly hit the underlying data source here.
         * Failing is safer than creating a cache stampede.
         */
        throw new IllegalStateException(
                "Unable to load pricing data because "
                        + "another request is rebuilding the cache. "
                        + "productId=" + productId
        );
    }
    /**
     * Loads the price from the underlying data source.
     * In the current project this is the in-memory product
     * price map. The same method can later call PostgreSQL
     * without changing the cache/lock flow.
     */
    private ProductPriceResponse loadPrice(
            Long productId
    ) {
        log.info(
                "Loading price from source for product {}",
                productId
        );
        BigDecimal unitPrice =
                PRODUCT_PRICES.get(productId);
        if (unitPrice == null) {
            productNotFoundCounter.increment();
            throw new ProductNotFoundException(
                    productId
            );
        }
        return new ProductPriceResponse(
                productId,
                unitPrice,
                "INR"
        );
    }
    private Cache getCache() {
        Cache cache =
                cacheManager.getCache(
                        CACHE_NAME
                );
        if (cache == null) {
            throw new IllegalStateException(
                    "Redis cache '" + CACHE_NAME
                            + "' is not configured"
            );
        }
        return cache;
    }
    private void sleep() {
        try {
            Thread.sleep(
                    retryDelay.toMillis()
            );
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(
                    "Pricing cache lock wait interrupted",
                    ex
            );
        }
    }
}