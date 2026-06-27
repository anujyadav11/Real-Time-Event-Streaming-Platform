package com.example.eventstream.order.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;
    @Column(name = "customer_name", nullable = false)
    private String customerName;
    @Column(name = "restaurant_name", nullable = false)
    private String restaurantName;
    @Column(name = "total_amount", nullable = false,precision = 10,scale = 2)
    private BigDecimal totalAmount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @PrePersist
    private void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
        if(createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if(status == null){
            status = OrderStatus.CREATED;
        }
    }
}
