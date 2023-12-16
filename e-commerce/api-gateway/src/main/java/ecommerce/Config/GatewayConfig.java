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
    private final AuthenticationFilter filter;

    public GatewayConfig(AuthenticationFilter filter) {
        this.filter = filter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder){
        return builder.routes()
                .route("auth-service-route", r -> r.path("/auth/register").uri("lb://auth-service"))
                .route("auth-service-route", r -> r.path("/auth/login").uri("lb://auth-service"))
                .route("users-route", r -> r.method(HttpMethod.POST, HttpMethod.GET)
                        .and()
                        .path("/users")
                        .uri("lb://users"))
                .route("users-route", r -> r.path("/users/**")
                        .and()
                        .predicate(serverWebExchange ->
                        {
                            String requestMethod = serverWebExchange.getRequest().getMethod().name();
                            return "PUT".equals(requestMethod) || "DELETE".equals(requestMethod);
                        })
                        .filters(f -> f.filter(filter.apply("ADMIN")))
                        .uri("lb://users"))
                .route("orders-route", r -> r.path("/orders/**").uri("lb://orders"))
                .route("products-route", r -> r.path("/products/**").uri("lb://products"))
                .route("reviews-route", r -> r.path("/reviews/**").uri("lb://reviews"))
                .route("wallet-route", r -> r.path("/wallet/**").uri("lb://wallet"))
                .route("payment-route", r -> r.path("/payment/**").uri("lb://payment"))
                .route("shipping-route", r -> r.path("/shipping/**").uri("lb://shipping"))
                .build();
    }
}
