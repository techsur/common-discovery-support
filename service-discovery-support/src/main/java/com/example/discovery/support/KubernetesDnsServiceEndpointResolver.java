package com.example.discovery.support;

import com.example.common.discovery.api.ServiceEndpointResolver;

import java.net.URI;
import java.util.Objects;

public class KubernetesDnsServiceEndpointResolver implements ServiceEndpointResolver {

    private final String scheme;
    private final String serviceSuffix;

    public KubernetesDnsServiceEndpointResolver(String scheme, String serviceSuffix) {
        this.scheme = normalizeScheme(scheme);
        this.serviceSuffix = serviceSuffix == null ? "" : serviceSuffix.trim();
    }

    @Override
    public URI resolve(String serviceName) {
        String normalizedName = normalizeServiceName(serviceName);
        String host = serviceSuffix.isBlank() ? normalizedName : normalizedName + "." + serviceSuffix;
        return URI.create(scheme + "://" + host);
    }

    private String normalizeScheme(String candidate) {
        String resolved = candidate == null || candidate.isBlank() ? "http" : candidate.trim().toLowerCase();
        return resolved;
    }

    private String normalizeServiceName(String serviceName) {
        return Objects.requireNonNull(serviceName, "serviceName must not be null").trim().toLowerCase();
    }
}

