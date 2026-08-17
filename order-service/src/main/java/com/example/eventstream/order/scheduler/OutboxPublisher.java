package com.example.eventstream.order.scheduler;

import com.example.eventstream.common.enums.OutBoxStatus;
import com.example.eventstream.order.entity.OutBoxEvent;
import com.example.eventstream.order.kafka.registry.EventPublisherRegistry;
import com.example.eventstream.order.metrics.OutboxMetrics;
import com.example.eventstream.order.repository.OutBoxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Publishes persisted outbox events.
 *
 * Business services only persist events into the Outbox table.
 * This scheduler is responsible for publishing them to Kafka.
 */
@Component
public class OutboxPublisher {

    private static final Logger log =
            LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutBoxRepository outBoxRepository;
    private final EventPublisherRegistry publisherRegistry;
    private final OutboxMetrics metrics;

    public OutboxPublisher(
            OutBoxRepository outBoxRepository,
            EventPublisherRegistry publisherRegistry,
            OutboxMetrics metrics
    ) {
        this.outBoxRepository = outBoxRepository;
        this.publisherRegistry = publisherRegistry;
        this.metrics = metrics;
    }
    @Scheduled(fixedDelayString = "${outbox.publisher.fixed-delay:5000}")
    @Transactional(transactionManager = "transactionManager")
    public void publishPendingEvents() {
        List<OutBoxEvent> events =
                outBoxRepository.findByStatusOrderByCreatedAtAsc(
                        OutBoxStatus.NEW
                );
        for (OutBoxEvent event : events) {
            try {
                log.info(
                        "Publishing outbox event {} of type {}",
                        event.getId(),
                        event.getEventType()
                );
                event.setStatus(OutBoxStatus.PROCESSING);
                outBoxRepository.save(event);

                publisherRegistry
                        .get(event.getEventType())
                        .publish(event);

                event.setStatus(OutBoxStatus.PUBLISHED);
                event.setPublishedAt(LocalDateTime.now());
                outBoxRepository.save(event);
                log.info(
                        "Outbox event {} published successfully.",
                        event.getId()
                );
                metrics.recovered();
            } catch (Exception ex) {
                log.error(
                        "Failed publishing outbox event {}",
                        event.getId(),
                        ex
                );
                event.setRetryCount(
                        event.getRetryCount() + 1
                );
                if (event.getRetryCount() >= 5) {
                    event.setStatus(OutBoxStatus.FAILED);
                } else {
                    event.setStatus(OutBoxStatus.NEW);
                }
                outBoxRepository.save(event);
            }
        }
    }
}