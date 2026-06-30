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

            // Auth / User management — public login + user CRUD
            .route("auth-service-route", r -> r
                .path("/auth/**", "/users/**")
                .uri("lb://auth-service"))

            // Trial Protocol service
            .route("trialprotocol-service-route", r -> r
                .path("/api/protocols/**")
                .uri("lb://trialprotocol-service"))

            // Subject Enrollment service
            .route("subject-enrollment-route", r -> r
                .path("/api/subjects/**")
                .uri("lb://SubjectEnrollment"))

            // Visit Scheduling service
            .route("visit-scheduling-route", r -> r
                .path("/api/visits/**")
                .uri("lb://visit-scheduling"))

            // Adverse Event (Pharmacovigilance) service
            .route("adverseevent-route", r -> r
                .path("/api/events/**")
                .uri("lb://adverseevent"))

            // Lab Sample & IP Tracking service
            .route("labsampleandiptracking-route", r -> r
                .path("/api/samples", "/api/samples/**", "/api/inventory", "/api/inventory/**")
                .uri("lb://labsampleandiptracking"))

            .build();
    }
}
