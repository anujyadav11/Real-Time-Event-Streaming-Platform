package com.example.eventstream.pricingservice.service;

import com.example.eventstream.pricingservice.dto.PriceResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class PricingService {
    private static final Map<Long, BigDecimal> PRODUCT_PRICES = Map.of(
            1L, BigDecimal.valueOf(299.99),
            2L, BigDecimal.valueOf(149.50),
            3L, BigDecimal.valueOf(799.00),
            4L, BigDecimal.valueOf(59.99)
    );
    public PriceResponse getPrice(Long productId){
        BigDecimal price = PRODUCT_PRICES.get(productId);

        if(price == null){
            throw new IllegalArgumentException("Product not found: " + productId);
        }
        return new PriceResponse(
          productId,
          price,
          "INR"
        );
    }
}
