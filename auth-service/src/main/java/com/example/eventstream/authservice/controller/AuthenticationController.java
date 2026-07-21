package com.example.eventstream.authservice.controller;

import com.example.eventstream.authservice.dto.request.LoginRequest;
import com.example.eventstream.authservice.dto.response.LoginResponse;
import com.example.eventstream.authservice.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService){
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user and returns a JWT access token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    public LoginResponse login( @Valid @RequestBody LoginRequest request ){
        return authenticationService.login(request);
    }
}
