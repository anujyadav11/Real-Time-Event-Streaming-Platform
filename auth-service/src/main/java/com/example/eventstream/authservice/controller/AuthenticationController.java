package com.example.eventstream.authservice.controller;

import com.example.eventstream.authservice.dto.request.LoginRequest;
import com.example.eventstream.authservice.dto.request.LogoutRequest;
import com.example.eventstream.authservice.dto.request.RefreshTokenRequest;
import com.example.eventstream.authservice.dto.response.*;
import com.example.eventstream.authservice.service.AdminService;
import com.example.eventstream.authservice.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh Access Token",
            description = "Generate a new access token using a valid refresh token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token refreshed"),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    public RefreshTokenResponse refresh(
            @Valid
            @RequestBody
            RefreshTokenRequest request) {
        return authenticationService.refresh(request);
    }
    @PostMapping("/logout")
    @Operation(
            summary = "Logout",
            description = "Revokes the refresh token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    public LogoutResponse logout(
            @Valid
            @RequestBody
            LogoutRequest request) {
        return authenticationService.logout(request);
    }
    @GetMapping("/sessions")
    public List<UserSessionResponse> sessions(
            Authentication authentication){
        return authenticationService.sessions(authentication);
    }
}
