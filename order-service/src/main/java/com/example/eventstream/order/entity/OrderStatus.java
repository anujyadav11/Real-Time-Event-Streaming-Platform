package com.example.eventstream.order.entity;

public enum OrderStatus {
    CREATED,
    INVENTORY_RESERVED,
    PAYMENT_PENDING,
    PAYMENT_COMPLETED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED,
}
