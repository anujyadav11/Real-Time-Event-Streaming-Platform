package com.example.eventstream.pricingservice.service;

import com.example.eventstream.common.dto.ProductPriceResponse;
import com.example.eventstream.pricingservice.exception.ProductNotFoundException;
import com.example.infrastructure.redis.DistributedLockService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

@Service
public class PricingService {
    private static final Logger log =
            LoggerFactory.getLogger(PricingService.class);

    private static final String LOCK_KEY_PREFIX =
            "lock:pricing:";
    /*
     * Current pricing data source.
     * In the future this can be replaced with a
     * PricingRepository without changing the cache
     * architecture.
     */
    private static final Map<Long, BigDecimal> PRODUCT_PRICES =
            Map.of(
                    1L, BigDecimal.valueOf(299.99),
                    2L, BigDecimal.valueOf(149.50),
                    3L, BigDecimal.valueOf(799.00),
                    4L, BigDecimal.valueOf(59.99)
            );

    private final PricingCacheService pricingCacheService;
    private final DistributedLockService distributedLockService;

    private final Counter cacheHitCounter;
    private final Counter cacheMissCounter;
    private final Counter negativeCacheHitCounter;
    private final Counter productNotFoundCounter;
    private final Counter lockAcquiredCounter;
    private final Counter lockWaitCounter;

    private final Duration lockTtl;
    private final Duration retryDelay;
    private final int maxRetries;

    public PricingService(
            PricingCacheService pricingCacheService,
            DistributedLockService distributedLockService,
            MeterRegistry meterRegistry,
            @Value("${pricing.cache.lock.ttl:5s}")
            Duration lockTtl,
            @Value("${pricing.cache.lock.retry-delay:50ms}")
            Duration retryDelay,
            @Value("${pricing.cache.lock.max-retries:20}")
            int maxRetries
    ) {
        this.pricingCacheService =
                pricingCacheService;

        this.distributedLockService =
                distributedLockService;

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

        this.negativeCacheHitCounter =
                meterRegistry.counter(
                        "pricing.cache.negative.hit"
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
    /**
     * Gets the price for a product.
     * Cache strategy
     * 1. Check positive cache.
     * 2. Check negative cache.
     * 3. Acquire distributed lock.
     * 4. Double-check both caches.
     * 5. Load from source.
     * 6. Populate appropriate cache.
     * 7. Release distributed lock.
     */
    public ProductPriceResponse getPrice(
            Long productId
    ) {
        /*
         * --------------------------------------------------
         * STEP 1
         * Check positive cache.
         * --------------------------------------------------
         */
        ProductPriceResponse cached =
                pricingCacheService.get(productId);
        if (cached != null) {
            cacheHitCounter.increment();
            log.debug(
                    "Pricing cache HIT for product {}",
                    productId
            );
            return cached;
        }
        cacheMissCounter.increment();
        /*
         * --------------------------------------------------
         * STEP 2
         * Check negative cache.
         * This prevents repeated source lookups for
         * nonexistent product IDs.
         * --------------------------------------------------
         */
        if (pricingCacheService.isMissing(productId)) {
            negativeCacheHitCounter.increment();
            log.debug(
                    "Pricing negative cache HIT for product {}",
                    productId
            );
            throw new ProductNotFoundException(
                    productId
            );
        }
        log.info(
                "Pricing cache MISS for product {}",
                productId
        );
        /*
         * --------------------------------------------------
         * STEP 3
         * Try to acquire distributed Redis lock.
         * The lock is per product, so:
         * product 1 → lock:pricing:1
         * product 2 → lock:pricing:2
         * Requests for different products don't block
         * each other.
         * --------------------------------------------------
         */
        String lockKey =
                LOCK_KEY_PREFIX + productId;
        String lockValue =
                distributedLockService.tryLock(
                        lockKey,
                        lockTtl
                );
        /*
         * --------------------------------------------------
         * We successfully acquired the lock.
         * --------------------------------------------------
         */
        if (lockValue != null) {
            lockAcquiredCounter.increment();
            try {
                /*
                 * --------------------------------------------------
                 * STEP 4
                 * Double-check positive cache.
                 *
                 * Another request may have populated Redis
                 * before we acquired the lock.
                 * --------------------------------------------------
                 */
                cached =
                        pricingCacheService.get(
                                productId
                        );
                if (cached != null) {
                    cacheHitCounter.increment();
                    log.debug(
                            "Pricing cache populated before lock "
                                    + "acquisition for product {}",
                            productId
                    );
                    return cached;
                }
                /*
                 * --------------------------------------------------
                 * STEP 5
                 * Double-check negative cache.
                 * --------------------------------------------------
                 */
                if (pricingCacheService.isMissing(
                        productId
                )) {
                    negativeCacheHitCounter.increment();
                    throw new ProductNotFoundException(
                            productId
                    );
                }
                /*
                 * --------------------------------------------------
                 * STEP 6
                 * Load data from the underlying source.
                 * --------------------------------------------------
                 */
                ProductPriceResponse response =
                        loadPrice(productId);
                /*
                 * --------------------------------------------------
                 * STEP 7
                 * Store fresh data in Redis.
                 *
                 * PricingCacheService also removes any
                 * negative-cache entry.
                 * --------------------------------------------------
                 */
                pricingCacheService.put(
                        productId,
                        response
                );
                log.info(
                        "Pricing cache populated for product {}",
                        productId
                );
                return response;
            } catch (ProductNotFoundException ex) {
                /*
                 * --------------------------------------------------
                 * Product doesn't exist.
                 *
                 * Store a short-lived negative-cache marker.
                 * --------------------------------------------------
                 */
                pricingCacheService.markMissing(
                        productId
                );
                throw ex;
            } finally {
                /*
                 * Always release the lock.
                 *
                 * DistributedLockService verifies ownership
                 * before deleting the Redis lock.
                 */
                distributedLockService.unlock(
                        lockKey,
                        lockValue
                );
            }
        }
        /*
         * --------------------------------------------------
         * Another instance/thread owns the lock.
         *
         * Don't hit the underlying source.
         * Wait for the lock owner to populate Redis.
         * --------------------------------------------------
         */
        lockWaitCounter.increment();
        for (
                int attempt = 1;
                attempt <= maxRetries;
                attempt++
        ) {
            sleep();
            /*
             * Check positive cache.
             */
            cached =
                    pricingCacheService.get(
                            productId
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
            /*
             * Check negative cache.
             */
            if (pricingCacheService.isMissing(
                    productId
            )) {
                negativeCacheHitCounter.increment();
                log.debug(
                        "Pricing negative cache HIT after waiting. "
                                + "productId={}, attempt={}",
                        productId,
                        attempt
                );
                throw new ProductNotFoundException(
                        productId
                );
            }
        }
        /*
         * --------------------------------------------------
         * The lock owner didn't populate either cache
         * within our allowed waiting period.
         *
         * We deliberately DON'T bypass the protection and
         * hit the source here because that could recreate
         * the cache stampede we're trying to prevent.
         * --------------------------------------------------
         */
        throw new IllegalStateException(
                "Unable to load pricing data because "
                        + "another request is rebuilding the cache. "
                        + "productId=" + productId
        );
    }
    /**
     * Loads pricing data from the underlying source.
     * Currently the project uses an in-memory map.
     * If this later becomes:
     * pricingRepository.findByProductId(productId)
     * the cache architecture does not need to change.
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
    /**
     * Prevents a waiting request from consuming CPU
     * while another instance rebuilds the cache.
     */
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