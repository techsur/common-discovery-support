package com.example.payment.client;

import com.example.common.discovery.api.ServiceEndpointResolver;
import com.example.payment.client.dto.NotificationRequest;
import com.example.payment.client.dto.NotificationResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class NotificationClient {

    private static final String NOTIFICATION_SERVICE = "notification-service";

    private final RestClient.Builder restClientBuilder;
    private final ServiceEndpointResolver serviceEndpointResolver;

    public NotificationClient(RestClient.Builder restClientBuilder, ServiceEndpointResolver serviceEndpointResolver) {
        this.restClientBuilder = restClientBuilder;
        this.serviceEndpointResolver = serviceEndpointResolver;
    }

    public NotificationResponse sendPaymentConfirmation(String orderId, String sku) {
        // payment-service is the upstream caller; notification-service is the downstream dependency.
        URI baseUri = serviceEndpointResolver.resolve(NOTIFICATION_SERVICE);
        URI requestUri = UriComponentsBuilder.fromUri(baseUri)
            .path("/notifications")
            .build()
            .toUri();

        NotificationRequest request = new NotificationRequest(
            orderId,
            "email",
            "Payment completed for order " + orderId + " and sku " + sku
        );

        try {
            NotificationResponse response = restClientBuilder.clone()
                .build()
                .post()
                .uri(requestUri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(NotificationResponse.class);

            if (response == null) {
                throw new RemoteServiceException("notification-service returned an empty response");
            }
            return response;
        } catch (RestClientResponseException exception) {
            throw new RemoteServiceException(
                "notification-service responded with status " + exception.getStatusCode().value(),
                exception
            );
        } catch (RestClientException exception) {
            throw new RemoteServiceException("notification-service call failed", exception);
        }
    }
}
