package com.example.eventstream.inventory;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
class InventoryController {
    @GetMapping("/{productId}")
    Map<String, Object> getStock(@PathVariable String productId) {
        return Map.of("productId", productId, "available", true, "quantity", 100);
    }
}
