package com.example.eventstream.order.security;

import com.example.eventstream.order.entity.Order;
import com.example.eventstream.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderSecurityService {

    private final OrderRepository repository;
    private final SecurityUtils securityUtils;

    public boolean canReadOrder(
            UUID orderId,
            Authentication authentication
    ) {
        Order order =
                repository.findById(orderId)
                        .orElseThrow();
        if (authentication.getAuthorities()
                .stream()
                .anyMatch(a ->
                        a.getAuthority()
                                .equals("ROLE_ADMIN"))) {
            return true;
        }
        UUID currentUser =
                securityUtils.getCurrentUserId(authentication);
        return order.getUserId().equals(currentUser);
    }
}