package com.example.notification.service;

import com.example.notification.model.NotificationRequest;
import com.example.notification.model.NotificationResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class NotificationService {

    public NotificationResponse sendNotification(NotificationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request must not be null");
        }
        if (request.orderId() == null || request.orderId().isBlank()) {
            throw new IllegalArgumentException("orderId must not be blank");
        }
        if (request.channel() == null || request.channel().isBlank()) {
            throw new IllegalArgumentException("channel must not be blank");
        }
        if (request.message() == null || request.message().isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }

        return new NotificationResponse(request.orderId(), request.channel(), "SENT", Instant.now());
    }
}

