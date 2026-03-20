package com.example.payment.client.dto;

import java.time.Instant;

public record NotificationResponse(
    String orderId,
    String channel,
    String status,
    Instant sentAt
) {
}

