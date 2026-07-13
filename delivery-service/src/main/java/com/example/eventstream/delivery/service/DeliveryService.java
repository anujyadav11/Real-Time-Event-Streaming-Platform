package com.example.eventstream.delivery.service;

import com.example.eventstream.common.command.CreateDeliveryCommand;
import com.example.eventstream.common.event.DeliveryAssignedEvent;
import com.example.eventstream.common.enums.DeliveryStatus;
import com.example.eventstream.delivery.dto.DeliveryResponse;
import com.example.eventstream.delivery.entity.Delivery;
import com.example.eventstream.delivery.exception.DeliveryNotFoundException;
import com.example.eventstream.delivery.kafka.producer.DeliveryEventProducer;
import com.example.eventstream.delivery.repository.DeliveryRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class DeliveryService {
    private static final Logger log = LoggerFactory.getLogger(DeliveryService.class);
    private final DeliveryRepository deliveryRepository;
    private final DeliveryEventProducer deliveryEventProducer;
    private final DeliveryAssignmentService deliveryAssignmentService;

    public DeliveryService (DeliveryRepository deliveryRepository , DeliveryEventProducer deliveryEventProducer, DeliveryAssignmentService deliveryAssignmentService){
        this.deliveryRepository = deliveryRepository;
        this.deliveryEventProducer = deliveryEventProducer;
        this.deliveryAssignmentService = deliveryAssignmentService;
    }
    @Transactional
    public void assignDelivery(CreateDeliveryCommand command) {
        log.info("Assigning delivery for order {}", command.orderId());
        Delivery delivery = Delivery.builder()
                .orderId(command.orderId())
                .deliveryPartner(deliveryAssignmentService.assignPartner())
                .status(DeliveryStatus.CREATED)
                .estimatedDeliveryTime(LocalDateTime.now().plusMinutes(30))
                .build();
        deliveryRepository.save(delivery);
        DeliveryAssignedEvent event =
                new DeliveryAssignedEvent(
                        UUID.randomUUID(),
                        command.orderId(),
                        delivery.getId(),
                        delivery.getDeliveryPartner(),
                        command.correlationId()
                );
        deliveryEventProducer
                .publishAssigned(event)
                .join();
        log.info("DeliveryAssignedEvent published for order {}",
                command.orderId());
    }
    public DeliveryResponse getDelivery(UUID orderId){
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new DeliveryNotFoundException(orderId));
                return new DeliveryResponse(
                        delivery.getOrderId(),
                        delivery.getDeliveryPartner(),
                        delivery.getStatus(),
                        delivery.getEstimatedDeliveryTime()
                );
    }
}
