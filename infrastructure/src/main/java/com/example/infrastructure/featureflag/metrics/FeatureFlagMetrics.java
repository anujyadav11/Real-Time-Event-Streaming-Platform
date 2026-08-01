package com.example.infrastructure.featureflag.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class FeatureFlagMetrics {
    private final MeterRegistry registry;
    public FeatureFlagMetrics(MeterRegistry registry) {
        this.registry = registry;
    }
    public void recordEnabled(String featureName) {
        Counter.builder("feature_flag_enabled_total")
                .description("Feature flag enabled executions")
                .tag("feature", featureName)
                .register(registry)
                .increment();
    }
    public void recordDisabled(String featureName) {
        Counter.builder("feature_flag_disabled_total")
                .description("Feature flag disabled executions")
                .tag("feature", featureName)
                .register(registry)
                .increment();
    }
}