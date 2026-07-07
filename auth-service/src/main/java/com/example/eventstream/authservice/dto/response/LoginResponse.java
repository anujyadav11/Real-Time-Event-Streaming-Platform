package com.example.eventstream.authservice.dto.response;

public record LoginResponse(
        String accessToken,
        String tokenType
) {
}
