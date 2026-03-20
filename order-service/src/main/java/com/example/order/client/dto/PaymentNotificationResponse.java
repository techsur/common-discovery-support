package com.example.order.client.dto;

import java.time.Instant;

public record PaymentNotificationResponse(
    String orderId,
    String channel,
    String status,
    Instant sentAt
) {
}

