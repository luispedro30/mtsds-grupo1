package ecommerce.Config;

import jakarta.ws.rs.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    private AuthenticationFilter filter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder){
        return builder.routes()
                .route("auth-service-route", r -> r.path("/auth/register")
                        .uri("lb://auth-service"))
                .route("auth-service-route", r -> r.path("/auth/login")
                        .uri("lb://auth-service"))
                .route("users-route", r -> r.method(HttpMethod.POST)
                        .and()
                        .path("/users")
                        .uri("lb://users"))
                .route("users-route", r -> r.method(HttpMethod.GET)
                        .and()
                        .path("/users/**")
                        .filters(f -> f.filters(filter))
                        .uri("lb://users"))
                .route("orders-route", r -> r.path("/orders/**")
                        .filters(f -> f.filters(filter))
                        .uri("lb://orders"))
                .route("products-route", r -> r.path("/products/**")
                        .uri("lb://products"))
                .route("reviews-route", r -> r.path("/reviews/**")
                        .filters(f -> f.filters(filter))
                        .uri("lb://reviews"))
                .route("wallet-route", r -> r.path("/wallet/**")
                        .filters(f -> f.filters(filter))
                        .uri("lb://wallet"))
                .route("payment-route", r -> r.path("/payment/**")
                        .filters(f -> f.filters(filter))
                        .uri("lb://payment"))
                .route("shipping-route", r -> r.path("/shipping/**")
                        .filters(f -> f.filters(filter))
                        .uri("lb://shipping"))
                .build();
    }

}
