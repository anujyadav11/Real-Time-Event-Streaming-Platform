package com.example.websocketservice.kafka.consumer;

import com.example.eventstream.common.event.DeliveryStatusUpdatedEvent;
import com.example.websocketservice.websocket.WebSocketPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DeliveryStatusConsumer {
    private static final Logger log = LoggerFactory.getLogger(DeliveryStatusConsumer.class);
    private final WebSocketPublisher webSocketPublisher;
    public DeliveryStatusConsumer(WebSocketPublisher webSocketPublisher){
        this.webSocketPublisher = webSocketPublisher;
    }
    @KafkaListener(
            topics = "KafkaTopic.DELIVERY_STATUS_UPDATED",
            groupId = "websocket-group"
    )
    public void consume(DeliveryStatusUpdatedEvent event){
        log.info("Received DeliveryStatusUpdatedEvent for order {}" , event.orderId());
        webSocketPublisher.publish(event);
    }
}
