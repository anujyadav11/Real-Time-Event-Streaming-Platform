package com.example.eventstream.delivery.controller;

import com.example.eventstream.delivery.dto.DeliveryResponse;
import com.example.eventstream.delivery.service.DeliveryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {
    
    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService){
        this.deliveryService = deliveryService;
    }

    @GetMapping("/{orderId}")
    public DeliveryResponse getDelivery(@PathVariable UUID orderId){
            return deliveryService.getDelivery(orderId);
    }
}
