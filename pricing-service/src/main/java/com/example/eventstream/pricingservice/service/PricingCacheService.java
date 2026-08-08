package com.example.eventstream.pricingservice.service;

import com.example.eventstream.common.dto.ProductPriceResponse;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

/**
 * Centralizes all Redis cache operations for Pricing Service.
 * Responsibilities:
 * - Read/write positive pricing cache
 * - Read/write negative cache
 * - Evict pricing cache
 * - Evict negative cache
 * PricingService should not need to know the actual Redis
 * cache names or how the cache is implemented.
 */
@Service
public class PricingCacheService {
    private static final String PRICE_CACHE =
            "product-prices";
    private static final String MISSING_CACHE =
            "product-prices-missing";
    private final CacheManager cacheManager;
    public PricingCacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    /**
     * Returns a cached product price.
     * @param productId product identifier
     * @return cached price, or null when not present
     */
    public ProductPriceResponse get(Long productId) {
        return getCache(PRICE_CACHE)
                .get(
                        productId,
                        ProductPriceResponse.class
                );
    }
    /**
     * Stores a product price in the positive cache.
     * Also removes any previous negative-cache entry.
     */
    public void put(
            Long productId,
            ProductPriceResponse response
    ) {
        getCache(PRICE_CACHE)
                .put(
                        productId,
                        response
                );
        /*
         * If this product previously didn't exist,
         * remove the old negative-cache marker.
         */
        getCache(MISSING_CACHE)
                .evict(productId);
    }
    /**
     * Checks whether the product is present in the
     * negative cache.
     * Negative caching prevents repeated lookups for
     * product IDs that don't exist.
     */
    public boolean isMissing(Long productId) {
        Boolean missing =
                getCache(MISSING_CACHE)
                        .get(
                                productId,
                                Boolean.class
                        );
        return Boolean.TRUE.equals(missing);
    }
    /**
     * Marks a product as missing.
     * The cache configuration controls the TTL of this
     * negative-cache entry.
     */
    public void markMissing(Long productId) {
        getCache(MISSING_CACHE)
                .put(
                        productId,
                        Boolean.TRUE
                );
    }
    /**
     * Removes both positive and negative cache entries.
     * This is important when the underlying product changes.
     * Example:
     * product exists
     *     ↓
     * price cached
     *     ↓
     * product updated
     *     ↓
     * evict(productId)
     *     ↓
     * next request loads fresh data
     */
    public void evict(Long productId) {
        getCache(PRICE_CACHE)
                .evict(productId);
        getCache(MISSING_CACHE)
                .evict(productId);
    }
    /**
     * Returns the configured Spring Cache instance.
     */
    private Cache getCache(String cacheName) {
        Cache cache =
                cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new IllegalStateException(
                    "Cache '" + cacheName
                            + "' is not configured"
            );
        }
        return cache;
    }
}