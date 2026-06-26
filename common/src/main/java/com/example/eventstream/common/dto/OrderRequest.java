package com.example.eventstream.common.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record OrderRequest(
        @NotBlank String customerId,
        @NotBlank String productId,
        @Min(1) int quantity,
        @NotNull BigDecimal amount
) {
}
