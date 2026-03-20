package com.example.order.model;

import java.time.Instant;

public record ErrorResponse(
    int status,
    String message,
    String path,
    Instant timestamp
) {
}
