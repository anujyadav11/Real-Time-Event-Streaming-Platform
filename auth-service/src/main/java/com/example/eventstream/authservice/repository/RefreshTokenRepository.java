package com.example.eventstream.authservice.repository;

import com.example.eventstream.authservice.entity.RefreshToken;
import com.example.eventstream.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(UUID token);
    List<RefreshToken> findAllByUserAndRevokedFalse(User user);
}