package com.example.websocketservice.websocket;

import com.example.eventstream.common.event.DeliveryStatusUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketPublisher {
    private static final Logger log = LoggerFactory.getLogger(WebSocketPublisher.class);

    private final SimpMessagingTemplate messagingTemplate;
    public WebSocketPublisher(SimpMessagingTemplate messagingTemplate){
        this.messagingTemplate = messagingTemplate;
    }
    public void publish(DeliveryStatusUpdatedEvent event){
        String destination = "/topic/orders/" + event.orderId();
        messagingTemplate.convertAndSend(destination, event);
        log.info("published webSocket update for order {} to {}", event.orderId(), destination);
    }
}
