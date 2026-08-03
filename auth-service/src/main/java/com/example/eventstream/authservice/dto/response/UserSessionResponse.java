package com.example.eventstream.authservice.dto.response;

import java.time.LocalDateTime;

public record UserSessionResponse(
        String device,
        String ip,
        LocalDateTime lastUsed,
        LocalDateTime expires
) {
}