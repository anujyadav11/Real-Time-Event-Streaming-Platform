package com.example.eventstream.authservice.service;

import com.example.eventstream.authservice.entity.RefreshToken;
import com.example.eventstream.authservice.entity.User;
import com.example.eventstream.authservice.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpiration;

    public RefreshTokenService(
            RefreshTokenRepository repository) {
        this.repository = repository;
    }
    public RefreshToken create(User user) {
        RefreshToken refreshToken =
                RefreshToken.builder()
                        .token(UUID.randomUUID())
                        .user(user)
                        .expiry(
                                LocalDateTime.now()
                                        .plusSeconds(refreshExpiration / 1000)
                        )
                        .revoked(false)
                        .build();
        return repository.save(refreshToken);
    }
}