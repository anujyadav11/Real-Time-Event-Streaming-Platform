package com.example.eventstream.authservice.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class SecurityMetrics {

    private final Counter accessDeniedCounter;
    private final Counter adminActionCounter;
    private final Counter invalidJwtCounter;

    public SecurityMetrics(MeterRegistry registry) {
        accessDeniedCounter = registry.counter("security_access_denied_total");
        adminActionCounter = registry.counter("security_admin_action_total");
        invalidJwtCounter = registry.counter("security_invalid_jwt_total");
    }

    public void accessDenied() {
        accessDeniedCounter.increment();
    }
    public void adminAction() {
        adminActionCounter.increment();
    }
    public void invalidJwt() {
        invalidJwtCounter.increment();
    }
}
