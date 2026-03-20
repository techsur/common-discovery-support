package com.example.payment.controller;

import com.example.payment.model.PaymentResponse;
import com.example.payment.service.PaymentProcessingService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentProcessingService paymentProcessingService;

    public PaymentController(PaymentProcessingService paymentProcessingService) {
        this.paymentProcessingService = paymentProcessingService;
    }

    @PostMapping("/{orderId}/charge")
    @ResponseStatus(HttpStatus.OK)
    public PaymentResponse charge(@PathVariable String orderId, @RequestParam String sku) {
        return paymentProcessingService.processPayment(orderId, sku);
    }
}

