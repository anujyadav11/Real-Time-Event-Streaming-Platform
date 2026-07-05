package com.example.eventstream.delivery.service;

import com.example.eventstream.common.event.PaymentCompletedEvent;
import com.example.eventstream.common.enums.DeliveryStatus;
import com.example.eventstream.delivery.dto.DeliveryResponse;
import com.example.eventstream.delivery.entity.Delivery;
import com.example.eventstream.delivery.exception.DeliveryNotFoundException;
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

    public DeliveryService (DeliveryRepository deliveryRepository){
        this.deliveryRepository = deliveryRepository;
    }
    @Transactional
    public void createDelivery(PaymentCompletedEvent event){
        log.info("Creating delivery for order: {}", event.orderId());

        Delivery delivery = Delivery.builder().
                orderId(event.orderId())
                .deliveryPartner(assignDeliveryPartner())
                .status(DeliveryStatus.CREATED)
                .estimatedDeliveryTime(LocalDateTime.now().plusMinutes(30))
                .build();
        deliveryRepository.save(delivery);
        log.info("Delivery created successfully for order : {}", event.orderId());
    }
    private String assignDeliveryPartner(){
        String[] partners = {
                "Rahul Sharma",
                "Amit Patel",
                "Neha Singh",
                "Rohit Verma",
                "Priya Mehta"
        };
        int index = (int)(Math.random() * partners.length);
        return partners[index];
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
