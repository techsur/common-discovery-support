package com.example.discovery.support.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "discovery.static")
public class StaticDiscoveryProperties {

    private final Map<String, URI> services = new LinkedHashMap<>();

    public Map<String, URI> getServices() {
        return services;
    }

    public Map<String, URI> asNormalizedServiceMap() {
        Map<String, URI> normalized = new LinkedHashMap<>();
        services.forEach((name, uri) -> normalized.put(name.trim().toLowerCase(), uri));
        return Map.copyOf(normalized);
    }
}

