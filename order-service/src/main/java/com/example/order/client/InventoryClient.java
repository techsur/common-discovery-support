package com.example.order.client;

import com.example.common.discovery.api.ServiceEndpointResolver;
import com.example.order.client.dto.InventoryAvailabilityResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class InventoryClient {

    private static final String INVENTORY_SERVICE = "inventory-service";

    private final RestClient.Builder restClientBuilder;
    private final ServiceEndpointResolver serviceEndpointResolver;

    public InventoryClient(RestClient.Builder restClientBuilder, ServiceEndpointResolver serviceEndpointResolver) {
        this.restClientBuilder = restClientBuilder;
        this.serviceEndpointResolver = serviceEndpointResolver;
    }

    public InventoryAvailabilityResponse checkAvailability(String sku) {
        // order-service is the upstream caller; inventory-service is the downstream dependency.
        URI baseUri = serviceEndpointResolver.resolve(INVENTORY_SERVICE);
        URI requestUri = UriComponentsBuilder.fromUri(baseUri)
            .path("/inventory/{sku}/availability")
            .buildAndExpand(sku)
            .toUri();

        try {
            InventoryAvailabilityResponse response = restClientBuilder.clone()
                .build()
                .get()
                .uri(requestUri)
                .retrieve()
                .body(InventoryAvailabilityResponse.class);

            if (response == null) {
                throw new RemoteServiceException("inventory-service returned an empty response");
            }
            return response;
        } catch (RestClientResponseException exception) {
            throw new RemoteServiceException(
                "inventory-service responded with status " + exception.getStatusCode().value(),
                exception
            );
        } catch (RestClientException exception) {
            throw new RemoteServiceException("inventory-service call failed", exception);
        }
    }
}
