package com.example.inventory.model;

public record InventoryAvailabilityResponse(
    String sku,
    boolean inStock,
    int availableQuantity
) {
}

