package com.example.eventstream.payment;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
class PaymentController {
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    Map<String, String> processPayment(@RequestBody Map<String, Object> paymentRequest) {
        return Map.of("status", "PAYMENT_ACCEPTED");
    }
}
