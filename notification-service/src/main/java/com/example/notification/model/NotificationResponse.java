package com.example.notification.model;

import java.time.Instant;

public record NotificationResponse(
    String orderId,
    String channel,
    String status,
    Instant sentAt
) {
}

