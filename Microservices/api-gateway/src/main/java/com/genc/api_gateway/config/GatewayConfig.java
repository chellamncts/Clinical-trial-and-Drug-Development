package com.genc.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()

            // Login endpoint — public, no JWT required
            .route("auth-service-route", r -> r
                .path("/auth/**")
                .uri("lb://auth-service"))

            // User management — protected, requires ADMIN JWT
            .route("users-route", r -> r
                .path("/users/**")
                .uri("lb://auth-service"))

            // Trial Protocol service — protected, requires valid JWT
            .route("trialprotocol-service-route", r -> r
                .path("/api/protocols/**")
                .uri("lb://trialprotocol-service"))

            .build();
    }
}

