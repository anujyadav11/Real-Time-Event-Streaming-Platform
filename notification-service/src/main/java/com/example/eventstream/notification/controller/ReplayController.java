package com.example.eventstream.notification.controller;

import com.example.eventstream.notification.dto.ReplayEventResponse;
import com.example.eventstream.notification.dto.ReplayResponse;
import com.example.eventstream.notification.service.ReplayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(
        name = "Dead Letter Replay",
        description = "Administrative APIs for replaying failed Kafka events."
)
@RestController
@RequestMapping("/admin/replay")
@RequiredArgsConstructor
public class ReplayController {

    private final ReplayService replayService;

    @PostMapping("/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Tag(name = "Replay", description = "Dead Letter Replay APIs")
    @Operation(
            summary = "Replay failed event",
            description = "Republishes a failed inbox event back to its original Kafka topic."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Replay started"),
            @ApiResponse(responseCode = "404", description = "Event not found"),
            @ApiResponse(responseCode = "409", description = "Replay not allowed")
    })
    public ResponseEntity<ReplayResponse> replay(
            @PathVariable UUID eventId
    ) {
        ReplayResponse response =
                replayService.replay(eventId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/failed")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all failed events",
            description = "Returns all failed inbox events that are eligible for replay."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Failed events retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied. Admin role required."
            )
    })
    public ResponseEntity<List<ReplayEventResponse>> failedEvents(){

        return ResponseEntity.ok(replayService.getFailedEvents());
    }

    @PostMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Replay all failed events",
            description = "Republishes all failed inbox events back to their original Kafka topics."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Replay request accepted"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied. Admin role required."
            )
    })
    public ResponseEntity<String> replayAll(){
        int replayed = replayService.replayAllFailedEvents();
        return ResponseEntity.ok(
                replayed + "failed events submitted for replay."
        );
    }
}