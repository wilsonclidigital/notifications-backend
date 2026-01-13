package com.ivansystems.notifications;

import org.springframework.web.bind.annotation.*;

import com.ivansystems.notifications.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
}
