package com.example.eventstream.authservice.entity;

import java.util.Set;

public enum Role {
    ADMIN(
            Set.of(
                    Permission.ORDER_READ,
                    Permission.ORDER_CREATE,
                    Permission.ORDER_UPDATE,
                    Permission.ORDER_DELETE,

                    Permission.PAYMENT_READ,
                    Permission.PAYMENT_PROCESS,
                    Permission.PAYMENT_REFUND,

                    Permission.INVENTORY_READ,
                    Permission.INVENTORY_UPDATE,

                    Permission.USER_READ,
                    Permission.USER_CREATE,
                    Permission.USER_UPDATE,
                    Permission.USER_DELETE
            )
    ),
    USER(
            Set.of(
                    Permission.ORDER_READ,
                    Permission.ORDER_CREATE,

                    Permission.PAYMENT_READ,

                    Permission.INVENTORY_READ
            )
    );
    private final Set<Permission> permissions;
    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }
    public Set<Permission> getPermissions() {
        return permissions;
    }
}