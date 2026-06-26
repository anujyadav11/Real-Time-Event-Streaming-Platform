package com.example.eventstream.tracking;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tracking")
class TrackingController {
    @GetMapping("/{orderId}")
    Map<String, String> trackOrder(@PathVariable String orderId) {
        return Map.of("orderId", orderId, "status", "ORDER_RECEIVED");
    }
}
