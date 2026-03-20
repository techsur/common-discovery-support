package com.example.common.discovery.api;

import java.net.URI;

public interface ServiceEndpointResolver {

    URI resolve(String serviceName);
}

