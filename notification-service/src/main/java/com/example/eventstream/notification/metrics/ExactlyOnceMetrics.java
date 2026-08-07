package com.example.eventstream.notification.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ExactlyOnceMetrics {
    private final Counter processed;
    private final Counter duplicates;
    public ExactlyOnceMetrics(
            MeterRegistry registry
    ){
        processed =
                registry.counter(
                        "exactly_once_processed_total"
                );
        duplicates =
                registry.counter(
                        "exactly_once_duplicate_total"
                );
    }
    public void processed(){
        processed.increment();
    }
    public void duplicate(){
        duplicates.increment();
    }
}