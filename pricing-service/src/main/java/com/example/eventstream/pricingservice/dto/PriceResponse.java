package com.example.eventstream.pricingservice.dto;

import java.math.BigDecimal;

public record PriceResponse(
        Long productId,
        BigDecimal price,
        String currency
) {
}
