package com.example.eventstream.notification.service;

import com.example.eventstream.common.enums.InboxStatus;
import com.example.eventstream.notification.entity.InboxEvent;
import com.example.eventstream.notification.repository.InboxEventRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InboxService {

    private static final Logger log =
            LoggerFactory.getLogger(InboxService.class);

    private final InboxEventRepository repository;

    @Transactional
    public void process(
            UUID eventId,
            String eventType,
            Runnable businessLogic
    ) {
        InboxEvent inboxEvent = InboxEvent.builder()
                .eventId(eventId)
                .eventType(eventType)
                .status(InboxStatus.PROCESSING)
                .processedAt(LocalDateTime.now())
                .build();
        try {
            // Persist first so duplicate events are rejected by the DB
            repository.saveAndFlush(inboxEvent);
        } catch (DataIntegrityViolationException ex) {
            log.info(
                    "Duplicate event {} ignored.",
                    eventId
            );
            return;
        }
        try {
            businessLogic.run();
            inboxEvent.setStatus(InboxStatus.COMPLETED);
        } catch (Exception ex) {
            inboxEvent.setStatus(InboxStatus.FAILED);
            log.error("Failed processing inbox event {}", eventId, ex);
            throw ex;
        }
    }
}