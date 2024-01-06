package ecommerce.Config;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.Models.EUserRole;
import ecommerce.Models.UserDto;
import ecommerce.Services.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<String> {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public GatewayFilter apply(@Nullable String roles) {

        if(roles == null)
            return TokenValidation();

        List<String> rolesArray = Arrays.asList(roles.split(","));// List of roles


        return Filter(rolesArray); // multiple roles provided

    }


    private GatewayFilter TokenValidation() {
        return (exchange, chain) -> {
            if (this.isAuthMissing(exchange.getRequest()))
                return this.onError(exchange, "Authorization header is missing in request", HttpStatus.UNAUTHORIZED);

            final String token = extractToken(exchange);

            if (jwtUtil.isInvalid(token)) {
                return this.onError(exchange, "Jwt token is invalid", HttpStatus.UNAUTHORIZED);
            }

            return chain.filter(exchange);
        };
    }

    private GatewayFilter Filter(List<String> rolesArray) {
        return (exchange, chain) -> {
            if (this.isAuthMissing(exchange.getRequest()))
                return this.onError(exchange, "Authorization header is missing in request", HttpStatus.UNAUTHORIZED);

            final String token = extractToken(exchange);

            if (jwtUtil.isInvalid(token)) {
                return this.onError(exchange, "Jwt token is invalid", HttpStatus.UNAUTHORIZED);
            }

            String roleFromToken = this.extractRoleFromToken(token);
            String idFromToken = this.extractIdFromToken(token);
            String userIdFromEndpoint = extractIdFromPath(exchange.getRequest().getPath().value().toString());
            System.out.println(idFromToken);
            System.out.println(roleFromToken);
            System.out.println(userIdFromEndpoint);
            System.out.println(exchange.getRequest().getPath());

            if (userIdFromEndpoint != null &&
                    (exchange.getRequest().getMethod() == HttpMethod.PUT ||
                            exchange.getRequest().getMethod() == HttpMethod.DELETE
                            || exchange.getRequest().getPath().toString().startsWith("/orders/user/")
                            || exchange.getRequest().getPath().toString().startsWith("/wallet/userId/"))) {
                if (!userIdFromEndpoint.equals(idFromToken)
                        && (roleFromToken.equals("USER") /*|| roleFromToken.equals("SUPPLIER")*/)) {
                    System.out.println("User must be the same");
                    return onError(exchange, "User must be the same", HttpStatus.UNAUTHORIZED);
                }
            }

            if(roleFromToken.equals("ADMIN") && rolesArray.contains(roleFromToken))
                return chain.filter(exchange);

            if(!rolesArray.contains(roleFromToken))
                return this.onError(exchange, "Token role does not have permissions to access this route", HttpStatus.UNAUTHORIZED);

            return chain.filter(exchange);
        };
    }

    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("Authorization").get(0);
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

    private String extractIdFromPath(String path) {
        String[] pathSegments = path.split("/");

        if (pathSegments.length > 0 && !pathSegments[pathSegments.length - 1].isEmpty()) {
            return pathSegments[pathSegments.length - 1]; // Last segment is the ID
        }

        return null; // ID not found in the path
    }



    private String extractToken(ServerWebExchange exchange) {
        return this.getAuthHeader(exchange.getRequest()).replace("Bearer ", "");
    }

    private String extractRoleFromToken(String token) {
        Claims claims = jwtUtil.getAllClaimsFromToken(token);
        return claims.get("role").toString();
    }

    private String extractIdFromToken(String token) {
        Claims claims = jwtUtil.getAllClaimsFromToken(token);
        return claims.get("id").toString();
    }

    private Mono<Void> onError(ServerWebExchange exchange, String errorMessage, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }
}