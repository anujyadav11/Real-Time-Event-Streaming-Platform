package com.example.eventstream.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        String username,

        @NotBlank
        String password
) {
}
