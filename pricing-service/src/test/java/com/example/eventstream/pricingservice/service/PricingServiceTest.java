package com.example.eventstream.pricingservice.service;

import com.example.eventstream.common.dto.ProductPriceResponse;
import com.example.eventstream.pricingservice.exception.ProductNotFoundException;
import com.example.infrastructure.redis.DistributedLockService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PricingServiceTest {
    private PricingService pricingService;
    @BeforeEach
    void setUp() {

        PricingCacheService pricingCacheService =
                mock(PricingCacheService.class);

        DistributedLockService distributedLockService =
                mock(DistributedLockService.class);

        MeterRegistry meterRegistry =
                new SimpleMeterRegistry();

        when(distributedLockService.tryLock(
                org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.any(Duration.class)
        )).thenReturn("test-lock");

        pricingService = new PricingService(
                pricingCacheService,
                distributedLockService,
                meterRegistry,
                Duration.ofSeconds(5),
                Duration.ofMillis(50),
                20
        );
    }
    @Test
    void returnsPriceForKnownProduct() {
        ProductPriceResponse response =
                pricingService.getPrice(1L);

        assertEquals(1L, response.productId());
        assertEquals(
                new BigDecimal("299.99"),
                response.unitPrice()
        );
        assertEquals(
                "INR",
                response.currency()
        );
    }
    @Test
    void rejectsUnknownProduct() {
        ProductNotFoundException exception =
                assertThrows(
                        ProductNotFoundException.class,
                        () -> pricingService.getPrice(99L)
                );

        assertEquals(
                "Product not found: 99",
                exception.getMessage()
        );
    }
}