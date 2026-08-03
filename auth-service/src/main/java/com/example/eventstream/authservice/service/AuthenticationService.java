package com.example.eventstream.authservice.service;

import com.example.eventstream.authservice.dto.request.LoginRequest;
import com.example.eventstream.authservice.dto.response.LoginResponse;
import com.example.eventstream.authservice.entity.RefreshToken;
import com.example.eventstream.authservice.entity.User;
import com.example.eventstream.authservice.repository.UserRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final Counter loginSuccessful;
    private final Counter loginFailure;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 JwtService jwtService, UserRepository userRepository, MeterRegistry meterRegistry,
                                 RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.loginSuccessful = meterRegistry.counter("jwt.login.success.total");
        this.loginFailure = meterRegistry.counter("jwt.login.fail.total");
        this.refreshTokenService = refreshTokenService;
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

            RefreshToken refreshToken = refreshTokenService.create(user);
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
}