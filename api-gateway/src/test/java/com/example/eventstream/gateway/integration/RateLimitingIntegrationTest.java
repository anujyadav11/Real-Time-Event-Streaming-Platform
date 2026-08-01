package com.example.eventstream.gateway.integration;

import com.example.eventstream.gateway.ApiGatewayApplication;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(
        classes = {ApiGatewayApplication.class, RateLimitingIntegrationTest.TestEndpoint.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.cloud.config.enabled=false",
                "eureka.client.enabled=false",
                "spring.cloud.discovery.enabled=false",
                "jwt.secret=test-only-secret-key-that-is-at-least-32-bytes-long",
                "gateway.rate-limit.replenish-rate=1",
                "gateway.rate-limit.burst-capacity=10",
                "gateway.rate-limit.requested-tokens=10",
                "spring.cloud.gateway.server.webflux.routes[0].id=rate-limit-test-route",
                "spring.cloud.gateway.server.webflux.routes[0].uri=forward:/test-backend",
                "spring.cloud.gateway.server.webflux.routes[0].predicates[0]=Path=/auth/rate-limit-test"
        }
)
@AutoConfigureWebTestClient
class RateLimitingIntegrationTest {

    private static final String REJECTED_METRIC = "gateway_rate_limit_rejected_total";

    @Container
    static final org.testcontainers.containers.GenericContainer<?> REDIS =
            TestRedisConfiguration.REDIS;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private MeterRegistry meterRegistry;

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", REDIS::getFirstMappedPort);
    }

    @Test
    void shouldAllowRequestWithinLimit() {
        getWithUniqueClient()
                .expectStatus().isOk()
                .expectHeader().exists("X-RateLimit-Remaining");
    }

    @Test
    void shouldRejectWhenLimitExceeded() {
        String clientIp = uniqueClientIp();

        get(clientIp).expectStatus().isOk();
        get(clientIp)
                .expectStatus().isEqualTo(429)
                .expectHeader().valueEquals("Retry-After", "1");
    }

    @Test
    void shouldReturnRateLimitHeaders() {
        String clientIp = uniqueClientIp();

        get(clientIp)
                .expectStatus().isOk()
                .expectHeader().exists("X-RateLimit-Remaining")
                .expectHeader().exists("X-RateLimit-Burst-Capacity");
        get(clientIp)
                .expectStatus().isEqualTo(429)
                .expectHeader().valueEquals("Retry-After", "1");
    }

    @Test
    void shouldReturnJsonError() {
        String clientIp = uniqueClientIp();

        get(clientIp).expectStatus().isOk();
        get(clientIp)
                .expectStatus().isEqualTo(429)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.status").isEqualTo(429)
                .jsonPath("$.message").isEqualTo("Rate limit exceeded. Please retry later.")
                .jsonPath("$.correlationId").exists()
                .jsonPath("$.timestamp").exists();
    }

    @Test
    void shouldIncrementMetric() {
        double before = rejectedRequestCount();
        String clientIp = uniqueClientIp();

        get(clientIp).expectStatus().isOk();
        get(clientIp).expectStatus().isEqualTo(429);

        assertThat(rejectedRequestCount()).isEqualTo(before + 1);
    }

    private WebTestClient.ResponseSpec getWithUniqueClient() {
        return get(uniqueClientIp());
    }

    private WebTestClient.ResponseSpec get(String clientIp) {
        return webTestClient.get()
                .uri("/auth/rate-limit-test")
                .header("X-Forwarded-For", clientIp)
                .header("X-Correlation-Id", "test-" + UUID.randomUUID())
                .exchange();
    }

    private String uniqueClientIp() {
        return "198.51.100." + (Math.abs(UUID.randomUUID().hashCode()) % 250 + 1);
    }

    private double rejectedRequestCount() {
        Counter counter = meterRegistry.find(REJECTED_METRIC).counter();
        return counter == null ? 0 : counter.count();
    }

    @RestController
    static class TestEndpoint {

        @GetMapping("/test-backend")
        String response() {
            return "ok";
        }
    }
}
