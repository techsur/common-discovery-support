package com.example.payment.service;

import com.example.payment.client.NotificationClient;
import com.example.payment.client.dto.NotificationResponse;
import com.example.payment.model.PaymentResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class PaymentProcessingService {

    private final NotificationClient notificationClient;

    public PaymentProcessingService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    public PaymentResponse processPayment(String orderId, String sku) {
        validate(orderId, sku);

        // payment-service forwards the success event downstream to notification-service.
        NotificationResponse notification = notificationClient.sendPaymentConfirmation(orderId, sku);
        return new PaymentResponse(orderId, sku, "CHARGED", Instant.now(), notification);
    }

    private void validate(String orderId, String sku) {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId must not be blank");
        }
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("sku must not be blank");
        }
    }
}
