package com.example.eventstream.inventory.kafka.consumer;

import com.example.eventstream.common.event.OrderCreatedEvent;
import com.example.eventstream.inventory.kafka.producer.InventoryEventProducer;
import com.example.eventstream.inventory.service.InventoryService;
import com.example.infrastructure.redis.IdempotencyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.concurrent.CompletableFuture;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class OrderCreatedConsumerTest {

    @Mock
    private InventoryEventProducer producer;

    @Mock
    private IdempotencyService idempotencyService;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private OrderCreatedConsumer consumer;

    @Test
    void reservesStockBeforePublishingTheReservationEvent() {
        OrderCreatedEvent event = orderCreatedEvent();
        when(idempotencyService.isProcessed("inventory-service", event.eventId())).thenReturn(false);
        when(producer.publishReserved(any()))
                .thenReturn(CompletableFuture.completedFuture(null));

        consumer.consume(event);

        InOrder order = inOrder(inventoryService, producer, idempotencyService);
        order.verify(inventoryService).reserveInventory(event.productId(), event.quantity());
        order.verify(producer).publishReserved(any());
        order.verify(idempotencyService).markProcessed("inventory-service", event.eventId());
    }

    @Test
    void ignoresAnAlreadyProcessedOrder() {
        OrderCreatedEvent event = orderCreatedEvent();
        when(idempotencyService.isProcessed("inventory-service", event.eventId())).thenReturn(true);

        consumer.consume(event);

        verifyNoInteractions(inventoryService, producer);
    }

    private OrderCreatedEvent orderCreatedEvent() {
        return new OrderCreatedEvent(
                UUID.randomUUID(), UUID.randomUUID(), "Customer", "Restaurant", 1L, 2,
                BigDecimal.TEN, "CREATED", LocalDateTime.now(), "correlation-id"
        );
    }
}
