package com.example.order.client.dto;

public record InventoryAvailabilityResponse(
    String sku,
    boolean inStock,
    int availableQuantity
) {
}

