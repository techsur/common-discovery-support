package com.example.order.client.dto;

import java.time.Instant;

public record PaymentResponse(
    String orderId,
    String sku,
    String paymentStatus,
    Instant chargedAt,
    PaymentNotificationResponse notification
) {
}

