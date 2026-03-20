package com.example.notification.model;

public record NotificationRequest(
    String orderId,
    String channel,
    String message
) {
}

