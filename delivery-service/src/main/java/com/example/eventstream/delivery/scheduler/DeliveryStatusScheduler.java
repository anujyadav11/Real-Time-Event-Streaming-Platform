package com.example.eventstream.delivery.scheduler;

import com.example.eventstream.common.event.DeliveryStatusUpdatedEvent;
import com.example.eventstream.common.enums.DeliveryStatus;
import com.example.eventstream.delivery.entity.Delivery;
import com.example.eventstream.delivery.kafka.producer.DeliveryEventProducer;
import com.example.eventstream.delivery.repository.DeliveryRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DeliveryStatusScheduler {

    private static final Logger log = LoggerFactory.getLogger(DeliveryStatusScheduler.class);

    private final DeliveryRepository deliveryRepository;
    private final DeliveryEventProducer producer;

    public DeliveryStatusScheduler(DeliveryRepository deliveryRepository,
                                   DeliveryEventProducer producer) {
        this.deliveryRepository = deliveryRepository;
        this.producer = producer;
    }
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void updateDeliveryStatus() {
        List<Delivery> deliveries =
                deliveryRepository.findByStatusNot(DeliveryStatus.DELIVERED);
        if (deliveries.isEmpty()) {
            return;
        }
        for (Delivery delivery : deliveries) {
            DeliveryStatus nextStatus = getNextStatus(delivery.getStatus());
            delivery.setStatus(nextStatus);
            if (nextStatus == DeliveryStatus.DELIVERED) {
                delivery.setDeliveredAt(LocalDateTime.now());
            }
            deliveryRepository.save(delivery);
            producer.publishStatusUpdated(new DeliveryStatusUpdatedEvent(
                    delivery.getOrderId(),
                    nextStatus,
                    LocalDateTime.now()
            )).join();
            log.info("Order {} status changed to {}",
                    delivery.getOrderId(),
                    nextStatus);
        }
    }
    private DeliveryStatus getNextStatus(DeliveryStatus currentStatus) {
        return switch (currentStatus) {
            case CREATED -> DeliveryStatus.DRIVER_ASSIGNED;
            case DRIVER_ASSIGNED -> DeliveryStatus.PICKED_UP;
            case PICKED_UP -> DeliveryStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY -> DeliveryStatus.DELIVERED;
            case DELIVERED -> DeliveryStatus.DELIVERED;
        };
    }
}
