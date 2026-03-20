package com.example.order.client;

import com.example.common.discovery.api.ServiceEndpointResolver;
import com.example.order.client.dto.PaymentResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class PaymentClient {

    private static final String PAYMENT_SERVICE = "payment-service";

    private final RestClient.Builder restClientBuilder;
    private final ServiceEndpointResolver serviceEndpointResolver;

    public PaymentClient(RestClient.Builder restClientBuilder, ServiceEndpointResolver serviceEndpointResolver) {
        this.restClientBuilder = restClientBuilder;
        this.serviceEndpointResolver = serviceEndpointResolver;
    }

    public PaymentResponse charge(String orderId, String sku) {
        // After stock is confirmed, order-service continues downstream to payment-service.
        URI baseUri = serviceEndpointResolver.resolve(PAYMENT_SERVICE);
        URI requestUri = UriComponentsBuilder.fromUri(baseUri)
            .path("/payments/{orderId}/charge")
            .queryParam("sku", sku)
            .buildAndExpand(orderId)
            .toUri();

        try {
            PaymentResponse response = restClientBuilder.clone()
                .build()
                .post()
                .uri(requestUri)
                .retrieve()
                .body(PaymentResponse.class);

            if (response == null) {
                throw new RemoteServiceException("payment-service returned an empty response");
            }
            return response;
        } catch (RestClientResponseException exception) {
            throw new RemoteServiceException(
                "payment-service responded with status " + exception.getStatusCode().value(),
                exception
            );
        } catch (RestClientException exception) {
            throw new RemoteServiceException("payment-service call failed", exception);
        }
    }
}
