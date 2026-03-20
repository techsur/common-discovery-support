package com.example.discovery.support;

import com.example.common.discovery.api.ServiceEndpointResolver;
import com.example.common.discovery.api.ServiceResolutionException;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

public class StaticServiceEndpointResolver implements ServiceEndpointResolver {

    private final Map<String, URI> serviceEndpoints;

    public StaticServiceEndpointResolver(Map<String, URI> serviceEndpoints) {
        this.serviceEndpoints = Objects.requireNonNull(serviceEndpoints, "serviceEndpoints must not be null");
    }

    @Override
    public URI resolve(String serviceName) {
        URI endpoint = serviceEndpoints.get(normalize(serviceName));
        if (endpoint == null) {
            throw new ServiceResolutionException("No static endpoint configured for service '" + serviceName + "'");
        }
        return endpoint;
    }

    private String normalize(String serviceName) {
        return Objects.requireNonNull(serviceName, "serviceName must not be null").trim().toLowerCase();
    }
}

