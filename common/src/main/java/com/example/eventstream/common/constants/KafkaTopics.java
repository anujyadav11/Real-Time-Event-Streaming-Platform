package com.example.eventstream.common.constants;

public final class KafkaTopics {
    public static final String ORDER_CREATED = "order-created";
    public static final String INVENTORY_RESERVED = "inventory-reserved";
    public static final String INVENTORY_RESERVATION_FAILED = "inventory-reservation-failed";
    public static final String PAYMENT_COMPLETED = "payment-completed";
    public static final String PAYMENT_FAILED = "payment-failed";
    public static final String DELIVERY_STATUS_UPDATED = "delivery-status-updated";
    public static final String RESERVE_INVENTORY_COMMAND =
            "reserve-inventory-command";
    public static final String PROCESS_PAYMENT_COMMAND =
            "process-payment-command";
    public static final String RELEASE_INVENTORY_COMMAND =
            "release-inventory-command";
    public static final String CREATE_DELIVERY_COMMAND =
            "create-delivery-command";
    public static final String SEND_NOTIFICATION_COMMAND =
            "send-notification-command";

    private KafkaTopics() {
    }
}
