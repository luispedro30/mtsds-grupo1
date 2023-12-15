package ecommerce.Config;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ecommerce.Models.EUserRole;
import ecommerce.Models.UserDto;
import ecommerce.Services.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RefreshScope
@Component
public class AuthenticationFilter implements GatewayFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (this.isAuthMissing(request))
            return this.onError(exchange, "Authorization header is missing in request", HttpStatus.UNAUTHORIZED);

        final String token = this.getAuthHeader(request).replace("Bearer ", "");

        if (jwtUtil.isInvalid(token)) {
            return this.onError(exchange, "Jwt token is invalid", HttpStatus.UNAUTHORIZED);
        }

        try {
            ServerWebExchange newExchange = this.populateRequestWithHeaders(exchange, token);
            return chain.filter(newExchange);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return this.onError(exchange, "Error while parsing authentication token", HttpStatus.UNAUTHORIZED);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders().getOrEmpty("Authorization").get(0);
    }

    private boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders().containsKey("Authorization");
    }

    private ServerWebExchange populateRequestWithHeaders(ServerWebExchange exchange, String token) throws IOException {
        Claims claims = jwtUtil.getAllClaimsFromToken(token);
        ObjectMapper mapper = new ObjectMapper();

        System.out.println(claims.get("claims"));
        System.out.println(claims.getSubject());
        System.out.println(claims.get("claims"));
        System.out.println(claims.get("login").toString());

        //UserDto userDto = mapper.readValue((JsonParser) claims.get("claims"), UserDto.class);
        UserDto userDto = new UserDto(claims.get("login").toString(),
                EUserRole.valueOf(claims.get("role").toString()));

        ServerHttpRequest newRequest = exchange.getRequest().mutate()
                .header("login", userDto.login().toString())
                .header("role", userDto.role().toString())
                .build();
        ServerWebExchange webExchange = exchange.mutate().request(newRequest).build();

        return webExchange;
    }
}
