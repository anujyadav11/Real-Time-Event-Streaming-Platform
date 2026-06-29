package com.example.eventstream.order.service;

import com.example.eventstream.order.entity.Order;
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
        log.info("Creating new order for customer: {}", order.getCustomerName());
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with id: {}", savedOrder.getId());
        OrderCreatedEvent event =
                new OrderCreatedEvent(
                        savedOrder.getId(),
                        savedOrder.getCustomerName(),
                        savedOrder.getRestaurantName(),
                        savedOrder.getTotalAmount(),
                        savedOrder.getStatus().name(),
                        savedOrder.getCreatedAt()
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
}
