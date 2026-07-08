package com.example.eventstream.order.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateOrderRequest(
        @NotBlank(message = "Customer name is required")
        String customerName,

        @NotBlank(message = "Restaurant name is required")
        String restaurantName,

        // @NotNull(message = "Total Amount is required")
        //@DecimalMin(value = "0.01", message = "Amount must be greater than zero")
       // BigDecimal totalAmount,

        @NotNull(message = "Product ID is required")
        Long productId,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
) {
}
