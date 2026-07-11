package com.example.eventstream.order.scheduler;

import com.example.eventstream.common.enums.OutBoxStatus;
import com.example.eventstream.common.event.OrderCreatedEvent;
import com.example.eventstream.order.entity.OutBoxEvent;
import com.example.eventstream.order.kafka.producer.OrderEventProducer;
import com.example.eventstream.order.repository.OutBoxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
/**
 * Publishes persisted order events. Keeping the database write and the event
 * publication separate prevents an unavailable Kafka broker from losing an order.
 */
@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutBoxRepository outBoxRepository;
    private final OrderEventProducer orderEventProducer;
    private final ObjectMapper objectMapper;

    public OutboxPublisher(OutBoxRepository outBoxRepository,
                           OrderEventProducer orderEventProducer,
                           ObjectMapper objectMapper) {
        this.outBoxRepository = outBoxRepository;
        this.orderEventProducer = orderEventProducer;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelayString = "${outbox.publisher.fixed-delay:5000}")
    @Transactional
    public void publishPendingEvents() {
        List<OutBoxEvent> events = outBoxRepository.findByStatusOrderByCreatedAtAsc(OutBoxStatus.NEW);
        for (OutBoxEvent event : events) {
            event.setStatus(OutBoxStatus.PROCESSING);
            outBoxRepository.save(event);
            if (!"ORDER_CREATED".equals(event.getEventType())) {
                log.warn("Skipping unsupported outbox event type {} with id {}",
                        event.getEventType(), event.getId());
                continue;
            }

            try {
                OrderCreatedEvent payload = objectMapper.readValue(
                        event.getPayload(), OrderCreatedEvent.class);
                orderEventProducer.publish(payload).join();
                event.setStatus(OutBoxStatus.PUBLISHED);
                event.setPublishedAt(LocalDateTime.now());
                outBoxRepository.save(event);
            } catch (Exception ex) {
                log.error("Failed to publish outbox event {}", event.getId(), ex);

                event.setRetryCount(event.getRetryCount() + 1);

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
