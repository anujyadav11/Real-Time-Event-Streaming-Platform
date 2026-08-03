package com.example.eventstream.authservice.service;

import com.example.eventstream.authservice.dto.request.LoginRequest;
import com.example.eventstream.authservice.dto.request.LogoutRequest;
import com.example.eventstream.authservice.dto.request.RefreshTokenRequest;
import com.example.eventstream.authservice.dto.response.LoginResponse;
import com.example.eventstream.authservice.dto.response.LogoutResponse;
import com.example.eventstream.authservice.dto.response.RefreshTokenResponse;
import com.example.eventstream.authservice.dto.response.UserSessionResponse;
import com.example.eventstream.authservice.entity.RefreshToken;
import com.example.eventstream.authservice.entity.User;
import com.example.eventstream.authservice.repository.RefreshTokenRepository;
import com.example.eventstream.authservice.repository.UserRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final Counter loginSuccessful;
    private final Counter loginFailure;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository repository;

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 JwtService jwtService, UserRepository userRepository, MeterRegistry meterRegistry,
                                 RefreshTokenService refreshTokenService, RefreshTokenRepository repository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.loginSuccessful = meterRegistry.counter("jwt.login.success.total");
        this.loginFailure = meterRegistry.counter("jwt.login.fail.total");
        this.refreshTokenService = refreshTokenService;
        this.repository = repository;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.username(),
                                    request.password()
                            )
                    );

            loginSuccessful.increment();

            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() ->
                            new IllegalStateException("Authenticated user not found"));

            String accessToken = jwtService.generateToken(user);

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            return new LoginResponse(
                    accessToken,
                    refreshToken.getToken().toString(),
                    "Bearer",
                    jwtService.getExpiration()
            );

        } catch (AuthenticationException ex) {
            loginFailure.increment();
            throw ex;
        }
    }
    public RefreshTokenResponse refresh(
            RefreshTokenRequest request ){
        RefreshToken currentToken =
                refreshTokenService.validateRefreshToken(
                        request.refreshToken());

        RefreshToken newRefreshToken =
                refreshTokenService.rotate(
                        currentToken);

        String accessToken =
                jwtService.generateToken(
                        newRefreshToken.getUser());

        return new RefreshTokenResponse(
                accessToken,
                newRefreshToken.getToken().toString(),
                "Bearer",
                jwtService.getExpiration()
        );
    }
    public LogoutResponse logout(
            LogoutRequest request ){
        refreshTokenService.revoke(request.refreshToken());
        return new LogoutResponse("Logged out successfully");
    }
    public List<UserSessionResponse> sessions(
            Authentication authentication) {
        User user =
                userRepository
                        .findByUsername(authentication.getName())
                        .orElseThrow();
        return repository
                .findAllByUserAndRevokedFalse(user)
                .stream()
                .map(token -> new UserSessionResponse(
                        token.getDeviceName(),
                        token.getIpAddress(),
                        token.getLastUsedAt(),
                        token.getExpiry()
                ))
                .toList();
    }
}