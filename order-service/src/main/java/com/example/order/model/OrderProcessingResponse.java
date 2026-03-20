package com.example.order.model;

import com.example.order.client.dto.InventoryAvailabilityResponse;
import com.example.order.client.dto.PaymentResponse;

import java.time.Instant;

public record OrderProcessingResponse(
    String orderId,
    String sku,
    String status,
    Instant processedAt,
    InventoryAvailabilityResponse inventory,
    PaymentResponse payment
) {

    public static OrderProcessingResponse completed(
        String orderId,
        String sku,
        InventoryAvailabilityResponse inventory,
        PaymentResponse payment
    ) {
        return new OrderProcessingResponse(orderId, sku, "ORDER_PROCESSED", Instant.now(), inventory, payment);
    }

    public static OrderProcessingResponse rejected(
        String orderId,
        String sku,
        InventoryAvailabilityResponse inventory
    ) {
        return new OrderProcessingResponse(orderId, sku, "OUT_OF_STOCK", Instant.now(), inventory, null);
    }
}

