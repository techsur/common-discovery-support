package com.example.discovery.support;

import com.example.common.discovery.api.ServiceEndpointResolver;
import com.example.common.discovery.api.ServiceResolutionException;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DiscoveryClientServiceEndpointResolver implements ServiceEndpointResolver {

    private final DiscoveryClient discoveryClient;
    private final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    public DiscoveryClientServiceEndpointResolver(DiscoveryClient discoveryClient) {
        this.discoveryClient = Objects.requireNonNull(discoveryClient, "discoveryClient must not be null");
    }

    @Override
    public URI resolve(String serviceName) {
        List<ServiceInstance> instances = findInstances(serviceName);
        if (instances.isEmpty()) {
            throw new ServiceResolutionException("No discovered instances available for service '" + serviceName + "'");
        }

        int index = Math.floorMod(
            counters.computeIfAbsent(normalize(serviceName), key -> new AtomicInteger()).getAndIncrement(),
            instances.size()
        );
        return instances.get(index).getUri();
    }

    private List<ServiceInstance> findInstances(String serviceName) {
        List<ServiceInstance> directMatches = discoveryClient.getInstances(serviceName);
        if (!directMatches.isEmpty()) {
            return directMatches;
        }

        String normalizedName = normalize(serviceName);
        return discoveryClient.getServices().stream()
            .filter(candidate -> normalize(candidate).equals(normalizedName))
            .findFirst()
            .map(discoveryClient::getInstances)
            .orElse(List.of());
    }

    private String normalize(String serviceName) {
        return Objects.requireNonNull(serviceName, "serviceName must not be null").trim().toLowerCase();
    }
}

