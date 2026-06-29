package com.example.eventstream.order.kafka.consumer;

import com.example.eventstream.common.event.InventoryReservedEvent;
import com.example.eventstream.order.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryReservedConsumer {
    private final OrderService orderService;
    public InventoryReservedConsumer(OrderService orderService) {
        this.orderService = orderService;
    }
    @KafkaListener(
            topics = "inventory-reserved",
            groupId = "order-group"
    )
    public void consume(InventoryReservedEvent event) {
        orderService.markInventoryReserved(event.orderId());
    }
}
