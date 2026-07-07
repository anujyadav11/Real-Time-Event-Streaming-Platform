package com.example.eventstream.authservice.service;

import com.example.eventstream.authservice.dto.request.LoginRequest;
import com.example.eventstream.authservice.dto.response.LoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    public AuthenticationService(AuthenticationManager authenticationManager, JwtService jwtService){
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }
    public LoginResponse login(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        String token = jwtService.generateToken(request.username());
        return new LoginResponse(token, "Bearer");
    }
}
