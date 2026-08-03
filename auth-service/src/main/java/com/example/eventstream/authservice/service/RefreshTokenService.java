package com.example.eventstream.authservice.service;

import com.example.eventstream.authservice.entity.RefreshToken;
import com.example.eventstream.authservice.entity.User;
import com.example.eventstream.authservice.repository.RefreshTokenRepository;
import com.example.eventstream.authservice.security.SessionContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final SessionContext sessionContext;

    @Value("${jwt.refresh-expiration:604800000}")
    private long refreshExpiration;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository, SessionContext sessionContext) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.sessionContext = sessionContext;
    }
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken =
                RefreshToken.builder()
                        .token(UUID.randomUUID())
                        .user(user)
                        .expiry(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
                        .revoked(false)
                        .deviceName("Unknown Device")
                        .userAgent(sessionContext.getUserAgent())
                        .ipAddress(sessionContext.getIpAddress())
                        .lastUsedAt(LocalDateTime.now())
                        .build();
        return refreshTokenRepository.save(refreshToken);
    }
    public RefreshToken validateRefreshToken(String token) {
        UUID refreshTokenId;
        try {
            refreshTokenId = UUID.fromString(token);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid refresh token format");
        }
        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(refreshTokenId)
                        .orElseThrow(() -> new IllegalArgumentException("RefreshToken Not found"));

        if(refreshToken.isRevoked()) {
            throw new IllegalArgumentException("Refresh Token is revoked");
        }
        if(refreshToken.getExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh Token is expired");
        }
        return refreshToken;
    }
    @Transactional
    public RefreshToken rotate(RefreshToken oldToken) {
        oldToken.setRevoked(true);
        oldToken.setLastUsedAt(LocalDateTime.now());
        refreshTokenRepository.save(oldToken);
        return createRefreshToken(oldToken.getUser());
    }
    @Transactional
    public void revoke(String token) {
        RefreshToken refreshToken = validateRefreshToken(token);
        refreshToken.setRevoked(true);
        refreshToken.setLastUsedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);
    }
    public List<RefreshToken> getActiveSessions(User user) {
        return refreshTokenRepository.findAllByUserAndRevokedFalse(user);
    }
}