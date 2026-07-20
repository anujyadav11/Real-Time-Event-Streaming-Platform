package com.example.eventstream.order.client;

import com.example.eventstream.common.dto.ProductPriceResponse;
import com.example.eventstream.order.exception.PricingServiceUnavailableException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class PricingClientTest {

    @Test
    void requestsPriceUsingConfiguredBaseUrl() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        PricingClient client = new PricingClient(builder, "http://PRICING-SERVICE");
        server.expect(once(), requestTo("http://PRICING-SERVICE/api/pricing/products/1"))
                .andRespond(withSuccess(
                        "{\"productId\":1,\"unitPrice\":299.99,\"currency\":\"INR\"}",
                        MediaType.APPLICATION_JSON));

        ProductPriceResponse response = client.getPrice(1L);

        assertEquals(1L, response.productId());
        assertEquals(new BigDecimal("299.99"), response.unitPrice());
        server.verify();
    }

    @Test
    void fallbackPreservesConnectionFailureAsCause() {
        PricingClient client = new PricingClient(
                RestClient.builder(), "http://PRICING-SERVICE");
        ResourceAccessException cause = new ResourceAccessException("connection refused");

        PricingServiceUnavailableException exception = assertThrows(
                PricingServiceUnavailableException.class,
                () -> client.fallbackPrice(1L, cause));

        assertSame(cause, exception.getCause());
    }
}
