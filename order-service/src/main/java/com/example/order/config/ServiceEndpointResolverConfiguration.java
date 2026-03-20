package com.example.order.config;

import com.example.common.discovery.api.ServiceEndpointResolver;
import com.example.discovery.support.DiscoveryClientServiceEndpointResolver;
import com.example.discovery.support.KubernetesDnsServiceEndpointResolver;
import com.example.discovery.support.StaticServiceEndpointResolver;
import com.example.discovery.support.config.KubernetesDiscoveryProperties;
import com.example.discovery.support.config.StaticDiscoveryProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
public class ServiceEndpointResolverConfiguration {

    @Bean
    @Profile("static-discovery")
    ServiceEndpointResolver staticServiceEndpointResolver(StaticDiscoveryProperties properties) {
        return new StaticServiceEndpointResolver(properties.asNormalizedServiceMap());
    }

    @Bean
    @Profile("eureka")
    ServiceEndpointResolver discoveryClientServiceEndpointResolver(DiscoveryClient discoveryClient) {
        return new DiscoveryClientServiceEndpointResolver(discoveryClient);
    }

    @Bean
    @Profile("k8s")
    ServiceEndpointResolver kubernetesDnsServiceEndpointResolver(KubernetesDiscoveryProperties properties) {
        return new KubernetesDnsServiceEndpointResolver(properties.getScheme(), properties.getServiceSuffix());
    }
}

