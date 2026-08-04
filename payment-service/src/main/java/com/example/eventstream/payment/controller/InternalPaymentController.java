package com.example.eventstream.payment.controller;

import com.example.infrastructure.security.internal.annotation.InternalAllowedServices;
import com.example.infrastructure.security.internal.identity.ServiceIdentity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/payment")
public class InternalPaymentController {
    @PostMapping("/process")
    @InternalAllowedServices({
            ServiceIdentity.ORDER_SERVICE,
            ServiceIdentity.GATEWAY
    })
    public void processPayment() {
    }
}
