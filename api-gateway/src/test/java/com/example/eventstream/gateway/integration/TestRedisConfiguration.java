package com.example.eventstream.gateway.integration;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Shared Redis container for gateway integration tests.
 *
 * <p>The test class registers its host and mapped port through
 * {@code @DynamicPropertySource}, which happens before Spring creates the
 * reactive Redis connection factory.</p>
 */
final class TestRedisConfiguration {

    static final GenericContainer<?> REDIS = new GenericContainer<>(
            DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    private TestRedisConfiguration() {
    }
}
