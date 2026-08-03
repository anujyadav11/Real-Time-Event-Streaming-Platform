package com.example.eventstream.authservice.dto.response;

import java.util.UUID;

public record UserSummaryResponse(
        UUID id,
        String username,
        String email,
        String role,
        boolean enabled
) {
}