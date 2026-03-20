package com.example.order.controller;

import com.example.order.model.OrderProcessingResponse;
import com.example.order.service.OrderProcessingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderProcessingService orderProcessingService;

    public OrderController(OrderProcessingService orderProcessingService) {
        this.orderProcessingService = orderProcessingService;
    }

    @PostMapping("/{orderId}/process")
    @ResponseStatus(HttpStatus.OK)
    public OrderProcessingResponse processOrder(@PathVariable String orderId, @RequestParam String sku) {
        return orderProcessingService.processOrder(orderId, sku);
    }
}

