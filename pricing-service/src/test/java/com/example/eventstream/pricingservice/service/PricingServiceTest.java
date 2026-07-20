package com.example.eventstream.pricingservice.service;

import com.example.eventstream.common.dto.ProductPriceResponse;
import com.example.eventstream.pricingservice.exception.ProductNotFoundException;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PricingServiceTest {

    private final PricingService pricingService =
            new PricingService(new SimpleMeterRegistry());

    @Test
    void returnsPriceForKnownProduct() {
        ProductPriceResponse response = pricingService.getPrice(1L);

        assertEquals(1L, response.productId());
        assertEquals(new BigDecimal("299.99"), response.unitPrice());
        assertEquals("INR", response.currency());
    }

    @Test
    void rejectsUnknownProduct() {
        ProductNotFoundException exception = assertThrows(
                ProductNotFoundException.class,
                () -> pricingService.getPrice(99L));

        assertEquals("Product not found: 99", exception.getMessage());
    }
}
