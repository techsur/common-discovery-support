package com.example.order;

import com.example.discovery.support.config.KubernetesDiscoveryProperties;
import com.example.discovery.support.config.StaticDiscoveryProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackageClasses = {
    StaticDiscoveryProperties.class,
    KubernetesDiscoveryProperties.class
})
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}

