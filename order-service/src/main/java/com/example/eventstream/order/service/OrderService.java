package com.example.eventstream.order.service;

import com.example.eventstream.common.event.PaymentCompletedEvent;
import com.example.eventstream.order.client.PricingClient;
import com.example.eventstream.order.dto.request.CreateOrderRequest;
import com.example.eventstream.common.dto.ProductPriceResponse;
import com.example.eventstream.order.entity.Order;
import com.example.eventstream.common.enums.OrderStatus;
import com.example.eventstream.order.exception.OrderNotFoundException;
import com.example.eventstream.common.event.OrderCreatedEvent;
import com.example.eventstream.order.mapper.OrderMapper;
import com.example.eventstream.order.repository.OrderRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final PricingClient pricingClient;
    private final OrderMapper orderMapper;
    private final MeterRegistry meterRegistry;
    private final OutboxService outboxService;

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    public OrderService(OrderRepository orderRepository,
                        PricingClient pricingClient, OrderMapper orderMapper, MeterRegistry meterRegistry, OutboxService outboxService) {
        this.orderRepository = orderRepository;
        this.pricingClient = pricingClient;
        this.orderMapper = orderMapper;
        this.meterRegistry = meterRegistry;
        this.outboxService = outboxService;
    }
    public Order createOrder(CreateOrderRequest request) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            String correlationId = UUID.randomUUID().toString();
            log.info("Creating new order for customer: {}", request.customerName());
            // Fetch product price
            ProductPriceResponse priceResponse =
                    pricingClient.getPrice(request.productId());
            log.info("Fetched unit price {} {} for product {}",
                    priceResponse.unitPrice(),
                    priceResponse.currency(),
                    request.productId());
            // Calculate total amount
            BigDecimal totalAmount = priceResponse.unitPrice()
                    .multiply(BigDecimal.valueOf(request.quantity()));
            // Map request to entity
            Order order = orderMapper.toEntity(request);
            order.setTotalAmount(totalAmount);
            order.setStatus(OrderStatus.CREATED);
            // Save
            Order savedOrder = orderRepository.save(order);
            log.info("Order created successfully with id: {}", savedOrder.getId());
            // Publish event
            OrderCreatedEvent event = new OrderCreatedEvent(
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
            outboxService.saveEvent(
                    savedOrder.getId(),
                    "ORDER",
                    "ORDER_CREATED",
                    event
            );
            log.info("OrderCreatedEvent saved to outbox for order {}", savedOrder.getId());
            meterRegistry.counter("orders.creation.success.total").increment();
            return savedOrder;
        } catch (Exception ex) {
            meterRegistry.counter("orders.creation.failed.total").increment();
            throw ex;
        } finally {
            sample.stop(
                    meterRegistry.timer("orders.creation.duration")
            );
        }
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
        int updated = orderRepository.updateStatusIfCurrentIn(
                orderId,
                OrderStatus.INVENTORY_RESERVED,
                Set.of(OrderStatus.CREATED));

        if (updated == 0) {
            Order order = getOrder(orderId);
            log.info(
                    "Ignoring stale inventory-reserved event for order {} because its status is already {}",
                    orderId,
                    order.getStatus());
            return;
        }

        log.info("Inventory reserved for order : {}", orderId);
    }
    public void markPaymentCompleted(PaymentCompletedEvent event) {
        log.info("Updating payment status for order {}", event.orderId());

        int updated = orderRepository.updateStatusIfCurrentIn(
                event.orderId(),
                OrderStatus.PAYMENT_COMPLETED,
                Set.of(
                        OrderStatus.CREATED,
                        OrderStatus.INVENTORY_RESERVED,
                        OrderStatus.PAYMENT_PENDING));

        if (updated == 0) {
            Order order = getOrder(event.orderId());
            log.info(
                    "Ignoring duplicate or stale payment-completed event for order {} because its status is already {}",
                    event.orderId(),
                    order.getStatus());
            return;
        }

        log.info("Order {} marked as PAID", event.orderId());
    }
}
