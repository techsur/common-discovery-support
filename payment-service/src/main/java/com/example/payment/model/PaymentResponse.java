package com.example.payment.model;

import com.example.payment.client.dto.NotificationResponse;

import java.time.Instant;

public record PaymentResponse(
    String orderId,
    String sku,
    String paymentStatus,
    Instant chargedAt,
    NotificationResponse notification
) {
}

