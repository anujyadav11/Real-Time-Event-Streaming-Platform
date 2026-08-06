package com.example.eventstream.notification.service;

import com.example.eventstream.common.command.SendNotificationCommand;
import com.example.eventstream.common.enums.InboxStatus;
import com.example.eventstream.notification.dto.ReplayResponse;
import com.example.eventstream.notification.entity.InboxEvent;
import com.example.eventstream.notification.kafka.producer.ReplayProducer;
import com.example.eventstream.notification.replay.EventTypeRegistry;
import com.example.eventstream.notification.repository.InboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReplayService {

    private final InboxEventRepository repository;
    private final ReplayProducer replayProducer;
    private final ObjectMapper objectMapper;
    private final EventTypeRegistry registry;

    public ReplayResponse replay(UUID eventId) {
        InboxEvent event =
                repository.findByEventId(eventId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Event not found."
                                ));
        switch (event.getStatus()) {
            case FAILED -> {
                // allowed
            }
            case PROCESSING -> throw new IllegalStateException("Event is currently being processed.");
            case COMPLETED -> throw new IllegalStateException("Completed events cannot be replayed.");
            default -> throw new IllegalStateException("Replay is not allowed.");
        }
        try {
            Class<?> type = registry.resolve(event.getEventType());

            Object payload = objectMapper.readValue(event.getPayload(), type);

            replayProducer.replay(
                    event.getTopic(),
                    payload
            );
            event.setReplayCount(
                    event.getReplayCount() + 1
            );
            event.setStatus(InboxStatus.PROCESSING);
            repository.save(event);

            return new ReplayResponse(
                    event.getEventId(),
                    event.getStatus().name(),
                    event.getReplayCount(),
                    LocalDateTime.now()
            );
        } catch (Exception ex) {
            throw new RuntimeException(
                    "Replay failed",
                    ex
            );
        }
    }
}