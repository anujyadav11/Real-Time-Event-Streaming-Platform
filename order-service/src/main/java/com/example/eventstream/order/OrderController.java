package com.example.eventstream.order;

import com.example.eventstream.common.dto.OrderRequest;
import com.example.eventstream.common.dto.OrderResponse;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
class OrderController {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    OrderResponse createOrder(@Valid @RequestBody OrderRequest request) {
        return new OrderResponse(UUID.randomUUID().toString(), "CREATED", Instant.now());
    }
}
