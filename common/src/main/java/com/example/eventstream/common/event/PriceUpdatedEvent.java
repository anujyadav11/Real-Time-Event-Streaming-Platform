package com.example.eventstream.common.event;

import java.math.BigDecimal;

public record PriceUpdatedEvent(
        Long productId,
        BigDecimal newPrice
) {
}