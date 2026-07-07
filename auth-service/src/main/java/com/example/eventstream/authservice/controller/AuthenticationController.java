package com.example.eventstream.authservice.controller;

import com.example.eventstream.authservice.dto.request.LoginRequest;
import com.example.eventstream.authservice.dto.response.LoginResponse;
import com.example.eventstream.authservice.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public LoginResponse login( @Valid @RequestBody LoginRequest request ){
        return authenticationService.login(request);
    }
}
