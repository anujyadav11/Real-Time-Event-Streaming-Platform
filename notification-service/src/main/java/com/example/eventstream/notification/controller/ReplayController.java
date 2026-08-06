package com.example.eventstream.notification.controller;

import com.example.eventstream.notification.dto.ReplayRequest;
import com.example.eventstream.notification.service.ReplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/replay")
@RequiredArgsConstructor
public class ReplayController {
    private final ReplayService replayService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN)")
    public ResponseEntity<String> replay(@RequestBody ReplayRequest request){
        replayService.replay(request);
        return ResponseEntity.accepted().build();
    }
}
