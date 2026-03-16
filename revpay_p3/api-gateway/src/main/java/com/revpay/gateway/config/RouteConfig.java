package com.revpay.gateway.config;

import com.revpay.gateway.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public RouteConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                // Auth service: no JWT needed
                .route("auth-service", r -> r.path("/api/auth/**").uri("lb://auth-service"))

                // Auth service: security/PIN endpoints (JWT required)
                .route("auth-security", r -> r
                        .path("/api/v1/security/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://auth-service"))

                // Card service: JWT required
                .route("card-service", r -> r
                        .path("/api/v1/cards/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://card-service"))

                // Wallet service
                .route("wallet-service", r -> r
                        .path("/api/v1/wallet/**", "/api/v1/transactions/**", "/api/v1/requests/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://wallet-service"))

                // Notification service
                .route("notification-service", r -> r
                        .path("/api/v1/notifications/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://notification-service"))

                // Business service: use X-User-Id header contract directly
                .route("business-service", r -> r
                        .path("/api/v1/business/**")
                        .uri("lb://business-service"))
                .build();
    }
}
