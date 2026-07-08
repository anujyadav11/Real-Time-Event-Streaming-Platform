package com.example.eventstream.common.dto;

import java.math.BigDecimal;

public record ProductPriceResponse(
        Long productId,
        BigDecimal unitPrice,
        String currency
) {
}
