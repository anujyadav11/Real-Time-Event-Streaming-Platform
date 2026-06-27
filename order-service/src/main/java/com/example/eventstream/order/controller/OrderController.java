package com.example.eventstream.order.controller;

import com.example.eventstream.order.dto.request.CreateOrderRequest;
import com.example.eventstream.order.dto.response.OrderResponse;
import com.example.eventstream.order.entity.Order;
import com.example.eventstream.order.mapper.OrderMapper;
import com.example.eventstream.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderMapper.toEntity(request);
        Order savedOrder = orderService.createOrder(order);
        return orderMapper.toResponse(savedOrder);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable UUID id) {
        Order order = orderService.getOrder(id);
        return orderMapper.toResponse(order);
    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
    }
}
