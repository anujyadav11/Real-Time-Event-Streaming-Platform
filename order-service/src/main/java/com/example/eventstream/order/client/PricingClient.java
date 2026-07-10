package com.example.eventstream.order.client;

import com.example.eventstream.common.dto.ProductPriceResponse;
import com.example.eventstream.order.exception.PricingServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class PricingClient {
    private static final Logger log = LoggerFactory.getLogger(PricingClient.class);
    private final RestClient restClient;

    public PricingClient(
            RestClient.Builder builder,
            @Value("${pricing-service.base-url}") String baseUrl) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .build();

    }
    @CircuitBreaker(
            name = "pricingService",
            fallbackMethod = "fallbackPrice"
    )
    public ProductPriceResponse getPrice(Long productId) {
        log.info("Calling Pricing Service for product {}", productId);

        return restClient.get()
                .uri("/api/pricing/products/{id}", productId)
                .retrieve()
                .body(ProductPriceResponse.class);
    }
    public ProductPriceResponse fallbackPrice(Long productId, Exception ex) {
        log.error("Pricing Service is unavailable for product {}", productId, ex);
        throw new PricingServiceUnavailableException(
                "Pricing Service is currently unavailable. Please try again later.",ex );
    }
}