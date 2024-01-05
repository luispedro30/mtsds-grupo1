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

    public GatewayConfig(AuthenticationFilter filter) {
        this.filter = filter;
    }




    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder){
        return builder.routes()
                .route("auth-service-route", r -> r.path("/auth/register")
                        .uri("lb://auth-service"))
                .route("auth-service-route", r -> r.path("/auth/login")
                        .uri("lb://auth-service"))
                .route("products-route", r -> r.method(HttpMethod.GET)
                        .and()
                        .path("/products/**")
                        .uri("lb://products"))
                .route("products-route", r -> r.method(HttpMethod.POST)
                        .and()
                        .path("/products")
                        .filters(f -> f.filter(filter.apply("SUPPLIER")))
                        .uri("lb://products"))
                .route("products-route", r -> r.method(HttpMethod.PUT)
                        .and()
                        .path("/products/**")
                        .filters(f -> f.filter(filter.apply("SUPPLIER,ADMIN")))
                        .uri("lb://products"))
                .route("users-route", r -> r.method(HttpMethod.POST)
                        .and()
                        .path("/users")
                        .uri("lb://users"))
                .route("users-route", r -> r.method(HttpMethod.GET)
                        .and()
                        .path("/users/**")
                        .uri("lb://users"))
                /*.route("users-route", r -> r.method(HttpMethod.PUT)
                        .and()
                        .path("/users/**")
                        .filters(f -> f.filter(filter.apply("USER,ADMIN")))
                        .uri("lb://users"))
                .route("users-route", r -> r.method(HttpMethod.DELETE)
                        .and()
                        .path("/users/**")
                        .filters(f -> f.filter(filter.apply("USER,ADMIN")))
                        .uri("lb://users"))*/
                .route("users-route", r -> r.path("/users/**")
                        .and()
                        .predicate(serverWebExchange ->
                        {
                            String requestMethod = serverWebExchange.getRequest().getMethod().name();
                            return "PUT".equals(requestMethod) || "DELETE".equals(requestMethod);
                        })
                        .filters(f -> f.filter(filter.apply("USER,SUPPLIER")))
                        .uri("lb://users"))
                .route("users-route", r -> r.path("/admin/users/**")
                        .and()
                        .predicate(serverWebExchange ->
                        {
                            String requestMethod = serverWebExchange.getRequest().getMethod().name();
                            return "PUT".equals(requestMethod) || "DELETE".equals(requestMethod);
                        })
                        .filters(f -> f.filter(filter.apply("ADMIN")))
                        .uri("lb://users"))
                .route("reviews-route", r -> r.method(HttpMethod.POST)
                        .and()
                        .path("/reviews")
                        .filters(f -> f.filter(filter.apply("USER")))
                        .uri("lb://reviews"))
                .route("reviews-route", r -> r.method(HttpMethod.GET)
                        .and()
                        .path("/reviews/**")
                        .uri("lb://reviews"))
                .route("wallet-route", r -> r.method(HttpMethod.GET)
                        .and()
                        .path("/wallet")
                        .filters(f -> f.filter(filter.apply("ADMIN")))
                        .uri("lb://wallet"))
                .route("wallet-route", r -> r.method(HttpMethod.GET)
                        .and()
                        .path("/wallet/**")
                        .filters(f -> f.filter(filter.apply("USER,ADMIN")))
                        .uri("lb://wallet"))
                .route("wallet-route", r -> r.method(HttpMethod.POST)
                        .and()
                        .path("/wallet")
                        .filters(f -> f.filter(filter.apply("USER")))
                        .uri("lb://wallet"))
                .route("wallet-route", r -> r.method(HttpMethod.GET)
                        .and()
                        .path("/wallet/**")
                        .uri("lb://wallet"))
                .route("payment-route", r -> r.method(HttpMethod.GET)
                        .and()
                        .path("/payment")
                        .filters(f -> f.filter(filter.apply("ADMIN")))
                        .uri("lb://wallet"))
                .route("payment-route", r -> r.method(HttpMethod.POST)
                        .and()
                        .path("/payment")
                        .filters(f -> f.filter(filter.apply("USER")))
                        .uri("lb://payment"))
                .route("payment-route", r -> r.method(HttpMethod.GET)
                        .and()
                        .path("/payment/**")
                        .filters(f -> f.filter(filter.apply("USER")))
                        .uri("lb://payment"))
                .route("shipping-route", r -> r.method(HttpMethod.GET)
                        .and()
                        .path("/shipping")
                        .filters(f -> f.filter(filter.apply("ADMIN,SUPPLIER")))
                        .uri("lb://shipping"))
                .route("shipping-route", r -> r.method(HttpMethod.POST)
                        .and()
                        .path("/shipping")
                        .filters(f -> f.filter(filter.apply("SUPPLIER")))
                        .uri("lb://shipping"))
                .route("shipping-route", r -> r.method(HttpMethod.GET)
                        .and()
                        .path("/shipping/**")
                        .filters(f -> f.filter(filter.apply("USER,SUPPLIER,ADMIN")))
                        .uri("lb://shipping"))
                .route("shipping-route", r -> r.method(HttpMethod.PUT)
                        .and()
                        .path("/shipping/**")
                        .filters(f -> f.filter(filter.apply("SUPPLIER,ADMIN")))
                        .uri("lb://shipping"))
                .route("orders-route", r -> r.method(HttpMethod.GET)
                        .and()
                        .path("/orders")
                        .filters(f -> f.filter(filter.apply("ADMIN")))
                        .uri("lb://orders"))
                .route("orders-route", r -> r.method(HttpMethod.POST)
                        .and()
                        .path("/orders")
                        .filters(f -> f.filter(filter.apply("USER")))
                        .uri("lb://orders"))
                .route("orders-route", r -> r.method(HttpMethod.GET)
                        .and()
                        .path("/orders/**")
                        .filters(f -> f.filter(filter.apply("USER")))
                        .uri("lb://orders"))
                .route("email-route", r -> r.path("/sending-email")
                        .uri("lb://email"))
                .route("email-route", r -> r.path("/emails/**")
                        .uri("lb://email"))
                .build();
    }

}
