package com.example.eventstream.order.service;

import com.example.eventstream.common.event.PaymentCompletedEvent;
import com.example.eventstream.order.entity.Order;
import com.example.eventstream.common.enums.OrderStatus;
import com.example.eventstream.order.exception.OrderNotFoundException;
import com.example.eventstream.common.event.OrderCreatedEvent;
import com.example.eventstream.order.kafka.producer.OrderEventProducer;
import com.example.eventstream.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public OrderService(OrderRepository orderRepository, OrderEventProducer orderEventProducer) {
        this.orderRepository = orderRepository;
        this.orderEventProducer = orderEventProducer;
    }
    public Order createOrder(Order order) {
        String correlationId = UUID.randomUUID().toString();
        log.info("Creating new order for customer: {}", order.getCustomerName());
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with id: {}", savedOrder.getId());
        OrderCreatedEvent event =
                new OrderCreatedEvent(
                        UUID.randomUUID(),
                        savedOrder.getId(),
                        savedOrder.getCustomerName(),
                        savedOrder.getRestaurantName(),
                        savedOrder.getTotalAmount(),
                        savedOrder.getStatus().name(),
                        savedOrder.getCreatedAt(),
                        correlationId
                );
        orderEventProducer.publish(event);
        return savedOrder;
    }
    public Order getOrder(UUID id) {
        log.info("Fetching order with id: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Order with id {} not found", id);
                    return new OrderNotFoundException(id);
        });
        log.info("Order {} fetched successfully",id);
        return order;
    }
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    public void deleteOrder(UUID id) {
        log.info("Deleting order with id: {}", id);
        if(!orderRepository.existsById(id)) {
            log.warn("Delete failed. Order {} does not exist", id);
            throw new OrderNotFoundException(id);
        }
        orderRepository.deleteById(id);
        log.info("Order deleted successfully with id: {}", id);
    }
    public void markInventoryReserved(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setStatus(OrderStatus.INVENTORY_RESERVED);

        orderRepository.save(order);

        log.info("Inventory reserved for order : {}", orderId);
    }
    public void markPaymentCompleted(PaymentCompletedEvent event) {
        log.info("Updating payment status for order {}", event.orderId());

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new OrderNotFoundException(event.orderId()));

        order.setStatus(OrderStatus.PAYMENT_COMPLETED);

        orderRepository.save(order);
        
        log.info("Order {} marked as PAID", order.getId());
    }
}
