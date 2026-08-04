package com.example.eventstream.order.controller;

import com.example.infrastructure.security.internal.context.InternalRequestContext;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/orders")
public class InternalOrderController {
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}