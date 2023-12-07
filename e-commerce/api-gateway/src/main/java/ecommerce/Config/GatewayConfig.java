package ecommerce.Config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder){
        return builder.routes()
                .route("auth-service-route", r -> r.path("/auth/**").uri("lb://auth"))
                .route("users-route", r -> r.path("/users/**").uri("lb://users"))
                .route("orders-route", r -> r.path("/orders/**").uri("lb://orders"))
                .route("products-route", r -> r.path("/products/**").uri("lb://products"))
                .route("reviews-route", r -> r.path("/reviews/**").uri("lb://reviews"))
                .route("wallet-route", r -> r.path("/wallet/**").uri("lb://wallet"))
                .build();
    }
}
