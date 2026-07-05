package com.example.eventstream.order.repository;

import com.example.eventstream.order.entity.Order;
import com.example.eventstream.common.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByCustomerName(String customerName);
    List<Order> findByRestaurantName(String restaurantName);
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);
}
