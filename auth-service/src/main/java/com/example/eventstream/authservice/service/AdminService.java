package com.example.eventstream.authservice.service;

import com.example.eventstream.authservice.dto.response.UserSummaryResponse;
import com.example.eventstream.authservice.entity.User;
import com.example.eventstream.authservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository repository;

    public List<UserSummaryResponse> getUsers() {
        return repository.findAll()
                .stream()
                .map(user ->
                        new UserSummaryResponse(
                                user.getId(),
                                user.getUsername(),
                                user.getEmail(),
                                user.getRole().name(),
                                user.isEnabled()
                        ))
                .toList();
    }
    @Transactional
    public void disable(UUID id){
        User user = repository.findById(id)
                .orElseThrow();
        user.setEnabled(false);
    }
}