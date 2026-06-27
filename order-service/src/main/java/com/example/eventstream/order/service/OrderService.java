package com.example.eventstream.order.service;

import com.example.eventstream.order.entity.Order;
import com.example.eventstream.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }
    public Order getOrder(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
    }
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    public void deleteOrder(UUID id) {
        if(!orderRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found");
        }
        orderRepository.deleteById(id);
    }
}
