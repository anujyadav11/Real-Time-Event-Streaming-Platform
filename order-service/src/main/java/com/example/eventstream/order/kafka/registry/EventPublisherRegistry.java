package com.example.eventstream.order.kafka.registry;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EventPublisherRegistry {
    private final Map<String, EventPublisher> publishers;
    public EventPublisherRegistry(List<EventPublisher> publishers) {
        this.publishers =
                publishers.stream()
                        .collect(Collectors.toConcurrentMap(
                                EventPublisher::supports,
                                Function.identity()
                        ));
    }
    public EventPublisher get(String eventType) {
        EventPublisher publisher = publishers.get(eventType);
        if(publisher == null) {
            throw new IllegalArgumentException("Unsupported Outbox event type: " + eventType);
        }
        return publisher;
    }
}
