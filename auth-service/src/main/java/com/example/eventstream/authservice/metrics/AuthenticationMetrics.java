package com.example.eventstream.authservice.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Gauge;
import com.example.eventstream.authservice.repository.RefreshTokenRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;


@Component
public class AuthenticationMetrics {
    private final Counter loginSuccess;
    private final Counter loginFailure;
    private final Counter refreshSuccess;
    private final Counter refreshFailure;
    private final Counter logoutSuccess;
    public AuthenticationMetrics(MeterRegistry registry, RefreshTokenRepository refreshTokenRepository) {
        loginSuccess =
                registry.counter("auth_login_success_total");
        loginFailure =
                registry.counter("auth_login_failure_total");
        refreshSuccess =
                registry.counter("auth_refresh_success_total");
        refreshFailure =
                registry.counter("auth_refresh_failure_total");
        logoutSuccess =
                registry.counter("auth_logout_total");
        Gauge.builder("auth_active_sessions", refreshTokenRepository,
                        repository -> repository.countByRevokedFalseAndExpiryAfter(LocalDateTime.now()))
                .register(registry);
    }
    public void loginSuccess() {
        loginSuccess.increment();
    }
    public void loginFailure() {
        loginFailure.increment();
    }
    public void refreshSuccess() {
        refreshSuccess.increment();
    }
    public void refreshFailure() {
        refreshFailure.increment();
    }
    public void logoutSuccess() {
        logoutSuccess.increment();
    }
}
