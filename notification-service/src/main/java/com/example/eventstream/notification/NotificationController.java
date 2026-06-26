package com.example.eventstream.notification;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
class NotificationController {
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    Map<String, String> sendNotification(@RequestBody Map<String, Object> notificationRequest) {
        return Map.of("status", "NOTIFICATION_QUEUED");
    }
}
