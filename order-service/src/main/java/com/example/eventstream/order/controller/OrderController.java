package com.example.eventstream.order.controller;

import com.example.eventstream.order.dto.request.CreateOrderRequest;
import com.example.eventstream.order.dto.response.OrderResponse;
import com.example.eventstream.order.entity.Order;
import com.example.eventstream.order.mapper.OrderMapper;
import com.example.eventstream.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order lifecycle APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    public OrderController(OrderService orderService, OrderMapper orderMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an order", description = "Creates a new order and starts order processing.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created"),
            @ApiResponse(responseCode = "400", description = "Invalid order request")
    })
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order savedOrder = orderService.createOrder(request);
        return orderMapper.toResponse(savedOrder);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an order", description = "Returns an order by its unique identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public OrderResponse getOrder(@PathVariable UUID id) {
        Order order = orderService.getOrder(id);
        return orderMapper.toResponse(order);
    }

    @GetMapping
    @Operation(summary = "List orders", description = "Returns all orders.")
    @ApiResponse(responseCode = "200", description = "Orders returned")
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an order", description = "Deletes an order by its unique identifier.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Order deleted"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public void deleteOrder(@PathVariable UUID id) {
        orderService.deleteOrder(id);
    }
}
