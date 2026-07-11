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
            .route("auth-service-route", r -> r
                .path("/auth/**", "/users/**")
                .uri("lb://auth-service"))

            .route("trialprotocol-service-route", r -> r
                .path("/api/protocols/**")
                .uri("lb://trialprotocol-service"))

            .route("subject-enrollment-route", r -> r
                .path("/api/subjects/**")
                .uri("lb://SubjectEnrollment"))

            .route("visit-scheduling-route", r -> r
                .path("/api/visits/**")
                .uri("lb://visit-scheduling"))

            .route("adverseevent-route", r -> r
                .path("/api/events/**")
                .uri("lb://adverseevent"))

            .route("lab-sample-service-route", r -> r
                .path("/api/samples", "/api/samples/**", "/api/inventory", "/api/inventory/**")
                .uri("lb://lab-sample-service"))

            .build();
    }
}
