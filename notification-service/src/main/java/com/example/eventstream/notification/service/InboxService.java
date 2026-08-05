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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InboxService {

    private static final Logger log =
            LoggerFactory.getLogger(InboxService.class);

    private final InboxEventRepository repository;

    @Transactional
    public InboxEvent createInbox(
            UUID eventId,
            String eventType
    ) {
        InboxEvent event = InboxEvent.builder()
                .eventId(eventId)
                .eventType(eventType)
                .status(InboxStatus.PROCESSING)
                .processedAt(LocalDateTime.now())
                .build();
        return repository.saveAndFlush(event);
    }
    @Transactional
    public void complete(InboxEvent event) {
        event.setStatus(InboxStatus.COMPLETED);
        repository.save(event);
    }
    @Transactional
    public void fail(InboxEvent event) {
        event.setStatus(InboxStatus.FAILED);
        repository.save(event);
    }

    public void process(
            UUID eventId,
            String eventType,
            Runnable businessLogic
    ) {
        InboxEvent event;
        try {
            event = createInbox(
                    eventId,
                    eventType
            );
        }
        catch (DataIntegrityViolationException ex){
            log.info(
                    "Duplicate event {} ignored.",
                    eventId);
            return;
        }
        try {
            businessLogic.run();
            complete(event);
        }
        catch (Exception ex){
            fail(event);
            throw ex;
        }
    }
    @Transactional
    public void recoverStuckEvents(){
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);

        List<InboxEvent> events =
                repository.findByStatusAndReceivedAtBefore(InboxStatus.PROCESSING, threshold);

        for(InboxEvent event : events){
            log.warn("Recovering stuck inbox event {}", event.getEventId());
            event.setStatus(InboxStatus.FAILED);
        }
    }
}