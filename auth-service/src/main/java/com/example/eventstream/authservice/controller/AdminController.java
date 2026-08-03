package com.example.eventstream.authservice.controller;

import com.example.eventstream.authservice.dto.response.UserSummaryResponse;
import com.example.eventstream.authservice.entity.Role;
import com.example.eventstream.authservice.entity.User;
import com.example.eventstream.authservice.repository.UserRepository;
import com.example.eventstream.authservice.service.AdminService;
import com.example.eventstream.common.security.annotation.CanReadUser;
import com.example.eventstream.common.security.annotation.CanUpdateUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;

    @GetMapping("/users")
    @CanReadUser
    public List<UserSummaryResponse> users() {
        return adminService.getUsers();
    }

    @PatchMapping("/users/{id}/disable")
    @CanUpdateUser
    public void disable(
            @PathVariable UUID id) {
        adminService.disable(id);
    }

    @Transactional
    public void updateRole(UUID id, Role role){
        User user = userRepository.findById(id)
                .orElseThrow();
        user.setRole(role);
    }
}
