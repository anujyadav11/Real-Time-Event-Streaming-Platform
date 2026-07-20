package com.example.eventstream.order.repository;

import com.example.eventstream.order.entity.Order;
import com.example.eventstream.common.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByCustomerName(String customerName);
    List<Order> findByRestaurantName(String restaurantName);
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Order o
            SET o.status = :newStatus
            WHERE o.id = :orderId
              AND o.status IN :allowedStatuses
            """)
    int updateStatusIfCurrentIn(
            @Param("orderId") UUID orderId,
            @Param("newStatus") OrderStatus newStatus,
            @Param("allowedStatuses") Collection<OrderStatus> allowedStatuses);
}
