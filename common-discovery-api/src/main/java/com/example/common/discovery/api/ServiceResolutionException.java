package com.example.common.discovery.api;

public class ServiceResolutionException extends RuntimeException {

    public ServiceResolutionException(String message) {
        super(message);
    }

    public ServiceResolutionException(String message, Throwable cause) {
        super(message, cause);
    }
}

