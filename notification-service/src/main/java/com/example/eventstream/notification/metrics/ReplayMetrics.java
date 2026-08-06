package com.example.eventstream.notification.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ReplayMetrics {

    private final Counter replaySuccess;
    private final Counter replayFailure;

    public ReplayMetrics(MeterRegistry registry) {
        replaySuccess = registry.counter(
                "replay_success_total"
        );
        replayFailure = registry.counter(
                "replay_failure_total"
        );
    }
    public void success() {
        replaySuccess.increment();
    }
    public void failure() {
        replayFailure.increment();
    }
}
