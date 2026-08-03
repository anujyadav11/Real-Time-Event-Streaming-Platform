package com.example.eventstream.authservice.entity;

public enum Permission {

    ORDER_READ,
    ORDER_CREATE,
    ORDER_UPDATE,
    ORDER_DELETE,

    PAYMENT_READ,
    PAYMENT_PROCESS,
    PAYMENT_REFUND,

    INVENTORY_READ,
    INVENTORY_UPDATE,

    USER_READ,
    USER_CREATE,
    USER_UPDATE,
    USER_DELETE

}