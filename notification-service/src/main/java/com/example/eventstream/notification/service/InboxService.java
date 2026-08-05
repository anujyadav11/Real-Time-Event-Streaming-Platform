package com.example.eventstream.notification.service;

import com.example.eventstream.notification.entity.InboxEvent;
import com.example.eventstream.notification.repository.InboxEventRepository;
import com.example.infrastructure.Inbox.exception.DuplicateEventException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InboxService {
    private final InboxEventRepository repository;
    @Transactional
    public void registerEvent(
            UUID eventId,
            String eventType
    ) {
        if (repository.existsByEventId(eventId)) {
            throw new DuplicateEventException(eventId);
        }
        repository.save(
                InboxEvent.builder()
                        .eventId(eventId)
                        .eventType(eventType)
                        .processedAt(LocalDateTime.now())
                        .build()
        );
    }
}