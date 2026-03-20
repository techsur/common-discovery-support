package com.example.notification.controller;

import com.example.notification.model.NotificationRequest;
import com.example.notification.model.NotificationResponse;
import com.example.notification.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public NotificationResponse sendNotification(@RequestBody NotificationRequest request) {
        return notificationService.sendNotification(request);
    }
}

