package com.example.eventstream.order.service;

import com.example.eventstream.common.event.PaymentCompletedEvent;
import com.example.eventstream.order.client.PricingClient;
import com.example.eventstream.order.dto.request.CreateOrderRequest;
import com.example.eventstream.common.dto.ProductPriceResponse;
import com.example.eventstream.order.entity.Order;
import com.example.eventstream.common.enums.OrderStatus;
import com.example.eventstream.order.exception.OrderNotFoundException;
import com.example.eventstream.common.event.OrderCreatedEvent;
import com.example.eventstream.order.kafka.producer.OrderEventProducer;
import com.example.eventstream.order.mapper.OrderMapper;
import com.example.eventstream.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;
    private final PricingClient pricingClient;
    private final OrderMapper orderMapper;

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public OrderService(OrderRepository orderRepository, OrderEventProducer orderEventProducer, PricingClient pricingClient, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderEventProducer = orderEventProducer;
        this.pricingClient = pricingClient;
        this.orderMapper = orderMapper;
    }
    public Order createOrder(CreateOrderRequest request) {
        String correlationId = UUID.randomUUID().toString();
        log.info("Creating new order for customer: {}", request.customerName());
        // 1. Fetch product price from Pricing Service
        ProductPriceResponse priceResponse = pricingClient.getPrice(request.productId());
        log.info("Fetched unit price {} {} for product {}",
                priceResponse.unitPrice(),
                priceResponse.currency(),
                request.productId());
        // 2. Calculate total amount
        BigDecimal totalAmount = priceResponse.unitPrice()
                .multiply(BigDecimal.valueOf(request.quantity()));
        // 3. Create Order entity
        Order order = orderMapper.toEntity(request);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.CREATED);
        // 4. Save order
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with id: {}", savedOrder.getId());
        // 5. Publish Kafka event
        OrderCreatedEvent event =
                new OrderCreatedEvent(
                        UUID.randomUUID(),
                        savedOrder.getId(),
                        savedOrder.getCustomerName(),
                        savedOrder.getRestaurantName(),
                        savedOrder.getProductId(),
                        savedOrder.getQuantity(),
                        savedOrder.getTotalAmount(),
                        savedOrder.getStatus().name(),
                        savedOrder.getCreatedAt(),
                        correlationId
                );
        orderEventProducer.publish(event);
        log.info("OrderCreatedEvent published for order {}", savedOrder.getId());
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
