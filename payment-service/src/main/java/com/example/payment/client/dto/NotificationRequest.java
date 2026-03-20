package com.example.payment.client.dto;

public record NotificationRequest(
    String orderId,
    String channel,
    String message
) {
}

