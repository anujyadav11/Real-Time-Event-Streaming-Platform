package com.example.eventstream.authservice.service;

import com.example.eventstream.authservice.dto.request.LoginRequest;
import com.example.eventstream.authservice.dto.response.LoginResponse;
import com.example.eventstream.authservice.entity.User;
import com.example.eventstream.authservice.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 JwtService jwtService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.username(),
                                request.password()
                        )
                );

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authentication user not found"));

        String token = jwtService.generateToken(user);

        return new LoginResponse(token, "Bearer");
    }
}