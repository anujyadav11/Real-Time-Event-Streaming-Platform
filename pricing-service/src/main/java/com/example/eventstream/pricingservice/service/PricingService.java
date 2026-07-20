package com.example.eventstream.pricingservice.service;

import com.example.eventstream.common.dto.ProductPriceResponse;
import com.example.eventstream.pricingservice.exception.ProductNotFoundException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class PricingService {
    private static final Logger log = LoggerFactory.getLogger(PricingService.class);
    private final Counter priceRequestCounter;
    private final Counter cacheMissCounter;
    private final Counter productNotFoundCounter;
    private static final Map<Long, BigDecimal> PRODUCT_PRICES = Map.of(
            1L, BigDecimal.valueOf(299.99),
            2L, BigDecimal.valueOf(149.50),
            3L, BigDecimal.valueOf(799.00),
            4L, BigDecimal.valueOf(59.99)
    );
    public PricingService(MeterRegistry meterRegistry) {
        this.priceRequestCounter = meterRegistry.counter(
                "pricing.requests.total");
        this.cacheMissCounter = meterRegistry.counter(
                "pricing.cache.miss");
        this.productNotFoundCounter = meterRegistry.counter(
                "pricing.product.not_found");
    }

    @Cacheable(value = "product-prices", key = "#productId")
    public ProductPriceResponse getPrice(Long productId) {
        priceRequestCounter.increment();
        log.info("Cache MISS - Fetching price for product {}", productId);
        BigDecimal unitPrice = PRODUCT_PRICES.get(productId);
        cacheMissCounter.increment();
        if (unitPrice == null) {
            productNotFoundCounter.increment();
            throw new ProductNotFoundException(productId);
        }
        return new ProductPriceResponse(
                productId,
                unitPrice,
                "INR"
        );
    }
}
