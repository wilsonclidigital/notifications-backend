package com.ivansystems.notifications.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ivansystems.notifications.dto.MessageRequest;
import com.ivansystems.notifications.model.NotificationLog;
import com.ivansystems.notifications.service.NotificationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@Valid @RequestBody MessageRequest request) {
        notificationService.processMessage(request);
        return ResponseEntity.ok("Message processed successfully");
    }

    @GetMapping("/logs")
    public ResponseEntity<List<NotificationLog>> getLogHistory() {
        return ResponseEntity.ok(notificationService.getLogHistory());
    }
}
