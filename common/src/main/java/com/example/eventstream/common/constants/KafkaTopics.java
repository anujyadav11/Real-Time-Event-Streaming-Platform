package com.example.eventstream.common.constants;

public final class KafkaTopics {
    public static final String ORDER_CREATED = "order-created";
    public static final String INVENTORY_RESERVED = "inventory-reserved";
    public static final String PAYMENT_COMPLETED = "payment-completed";
    public static final String DELIVERY_STATUS_UPDATED = "delivery-status-updated";

    private KafkaTopics() {
    }
}
