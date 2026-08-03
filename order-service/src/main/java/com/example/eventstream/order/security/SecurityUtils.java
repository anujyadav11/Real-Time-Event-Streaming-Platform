package com.example.eventstream.order.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtils {
    public UUID getCurrentUserId(
            Authentication authentication
    ) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return UUID.fromString(
                jwt.getClaimAsString("userId")
        );
    }
}