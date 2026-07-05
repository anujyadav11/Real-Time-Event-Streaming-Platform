package com.example.eventstream.delivery.kafka.producer;

import com.example.eventstream.common.constants.KafkaTopics;
import com.example.eventstream.common.event.DeliveryStatusUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DeliveryEventProducer {
    private static final Logger log = LoggerFactory.getLogger(DeliveryEventProducer.class);
    private final KafkaTemplate<String, DeliveryStatusUpdatedEvent> kafkaTemplate;
    public DeliveryEventProducer(KafkaTemplate<String, DeliveryStatusUpdatedEvent> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }
    public void publish(DeliveryStatusUpdatedEvent event){
        kafkaTemplate.send(
                KafkaTopics.DELIVERY_STATUS_UPDATED,
                event.orderId().toString(),
                event
        );
        log.info("Published DeliveryStatusUpdatedEvent for order: {}", event.orderId());
    }
}
