package com.example.eventstream.delivery.controller;

import com.example.eventstream.delivery.dto.DeliveryResponse;
import com.example.eventstream.delivery.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/deliveries")
@Tag(name = "Deliveries", description = "Order delivery tracking APIs")
@SecurityRequirement(name = "Bearer Authentication")
public class DeliveryController {
    
    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService){
        this.deliveryService = deliveryService;
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get delivery", description = "Returns delivery status for an order.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Delivery returned"),
            @ApiResponse(responseCode = "404", description = "Delivery not found")
    })
    public DeliveryResponse getDelivery(@PathVariable UUID orderId){
            return deliveryService.getDelivery(orderId);
    }
}
