package com.example.eventstream.order.integration;

import com.example.eventstream.common.enums.OrderStatus;
import com.example.eventstream.order.entity.Order;
import com.example.eventstream.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(PostgresContainerConfig.class)
public class OrderRepositoryIT {
    @Autowired
    private OrderRepository orderRepository;

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        PostgresContainerConfig.configure(registry);
    }

    @Test
    void shouldSaveOrder() {
        Order order = Order.builder()
                .customerName("Anuj")
                .restaurantName("Domino's")
                .productId(101L)
                .quantity(2)
                .totalAmount(BigDecimal.valueOf(499.99))
                .status(OrderStatus.CREATED)
                .build();
        Order savedOrder = orderRepository.save(order);
        Optional<Order> loadedOrder =
                orderRepository.findById(savedOrder.getId());
        assertTrue(loadedOrder.isPresent());
        assertEquals(
                "Anuj",
                loadedOrder.get().getCustomerName()
        );
        assertEquals(
                "Domino's",
                loadedOrder.get().getRestaurantName()
        );
        assertEquals(
                101,
                loadedOrder.get().getProductId()
        );
        assertEquals(
                2,
                loadedOrder.get().getQuantity()
        );
        assertEquals(
                BigDecimal.valueOf(499.99),
                loadedOrder.get().getTotalAmount()
        );
        assertEquals(
                OrderStatus.CREATED,
                loadedOrder.get().getStatus()
        );
    }

    @Test
    void shouldNotRegressPaymentStatusWhenInventoryEventArrivesLate() {
        Order order = Order.builder()
                .customerName("Anuj")
                .restaurantName("Domino's")
                .productId(101L)
                .quantity(2)
                .totalAmount(BigDecimal.valueOf(499.99))
                .status(OrderStatus.PAYMENT_COMPLETED)
                .build();
        Order savedOrder = orderRepository.saveAndFlush(order);

        int updated = orderRepository.updateStatusIfCurrentIn(
                savedOrder.getId(),
                OrderStatus.INVENTORY_RESERVED,
                Set.of(OrderStatus.CREATED));

        assertEquals(0, updated);
        assertEquals(
                OrderStatus.PAYMENT_COMPLETED,
                orderRepository.findById(savedOrder.getId()).orElseThrow().getStatus());
    }
}
