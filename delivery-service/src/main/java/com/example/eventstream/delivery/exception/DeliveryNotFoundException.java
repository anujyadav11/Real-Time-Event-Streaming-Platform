package com.example.eventstream.delivery.exception;

import java.util.UUID;

public class DeliveryNotFoundException extends RuntimeException {
    public DeliveryNotFoundException(UUID orderId) {
        super("Delivery for order id " + orderId + " not found");
    }
}
