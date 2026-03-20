package com.example.inventory.controller;

import com.example.inventory.model.InventoryAvailabilityResponse;
import com.example.inventory.service.InventoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{sku}/availability")
    public InventoryAvailabilityResponse checkAvailability(@PathVariable String sku) {
        return inventoryService.checkAvailability(sku);
    }
}

