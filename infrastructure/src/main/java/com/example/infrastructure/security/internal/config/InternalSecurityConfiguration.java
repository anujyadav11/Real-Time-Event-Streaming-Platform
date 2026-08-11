package com.example.infrastructure.security.internal.config;

import com.example.infrastructure.security.internal.filter.InternalApiKeyFilter;
import com.example.infrastructure.security.internal.metrics.InternalSecurityMetrics;
import com.example.infrastructure.security.internal.properties.InternalSecurityProperties;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(InternalSecurityProperties.class)
public class InternalSecurityConfiguration {

    @Bean
    public InternalSecurityMetrics internalSecurityMetrics(
            MeterRegistry registry) {
        return new InternalSecurityMetrics(registry);
    }
    @Bean
    public InternalApiKeyFilter internalApiKeyFilter(
            InternalSecurityProperties properties,
            InternalSecurityMetrics metrics) {
        return new InternalApiKeyFilter(properties, metrics);
    }
}