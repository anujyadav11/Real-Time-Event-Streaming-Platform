package com.example.eventstream.order.entity;

public enum OrderStatus {
    CREATED,
    PAYMENT_PENDING,
    PAYMENT_COMPLETED,
    INVENTORY_RESERVED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED,
}
