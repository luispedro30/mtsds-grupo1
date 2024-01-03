package ecommerce;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
@OpenAPIDefinition(
        info = @Info(
                title = "Authentication Service",
                description = "OpenAPI documentation for the Inventory Service."
        )
)
public class Authentication {
    public static void main(String[] args) {
        SpringApplication.run(Authentication.class, args);
        System.out.println("Hello world!");
    }
}