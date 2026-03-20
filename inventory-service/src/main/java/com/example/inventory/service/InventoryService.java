package com.example.inventory.service;

import com.example.inventory.model.InventoryAvailabilityResponse;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    public InventoryAvailabilityResponse checkAvailability(String sku) {
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("sku must not be blank");
        }

        String normalizedSku = sku.trim().toUpperCase();
        boolean inStock = !normalizedSku.startsWith("OOS") && !normalizedSku.endsWith("-OUT");
        int availableQuantity = inStock ? Math.floorMod(normalizedSku.hashCode(), 20) + 1 : 0;
        return new InventoryAvailabilityResponse(sku, inStock, availableQuantity);
    }
}

