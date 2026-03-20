package com.example.order.service;

import com.example.order.client.InventoryClient;
import com.example.order.client.PaymentClient;
import com.example.order.client.dto.InventoryAvailabilityResponse;
import com.example.order.client.dto.PaymentResponse;
import com.example.order.model.OrderProcessingResponse;
import org.springframework.stereotype.Service;

@Service
public class OrderProcessingService {

    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;

    public OrderProcessingService(InventoryClient inventoryClient, PaymentClient paymentClient) {
        this.inventoryClient = inventoryClient;
        this.paymentClient = paymentClient;
    }

    public OrderProcessingResponse processOrder(String orderId, String sku) {
        validate(orderId, sku);

        // First downstream hop: order-service checks inventory-service for stock availability.
        InventoryAvailabilityResponse inventory = inventoryClient.checkAvailability(sku);
        if (!inventory.inStock()) {
            return OrderProcessingResponse.rejected(orderId, sku, inventory);
        }

        // Second downstream hop: order-service calls payment-service only after stock is available.
        PaymentResponse payment = paymentClient.charge(orderId, sku);
        return OrderProcessingResponse.completed(orderId, sku, inventory, payment);
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
