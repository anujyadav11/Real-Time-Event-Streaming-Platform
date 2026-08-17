package com.example.eventstream.notification.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import com.example.eventstream.common.enums.InboxStatus;
import com.example.eventstream.notification.repository.InboxEventRepository;
import org.springframework.stereotype.Component;

@Component
public class ReplayMetrics {

    private final Counter replaySuccess;
    private final Counter replayFailure;

    public ReplayMetrics(MeterRegistry registry, InboxEventRepository repository) {
        replaySuccess = registry.counter(
                "replay_success_total"
        );
        replayFailure = registry.counter(
                "replay_failure_total"
        );
        Gauge.builder("replay_failed_events", repository,
                        repo -> repo.countByStatus(InboxStatus.FAILED))
                .description("Inbox events currently eligible for replay")
                .register(registry);
    }
    public void success() {
        replaySuccess.increment();
    }
    public void failure() {
        replayFailure.increment();
    }
}
