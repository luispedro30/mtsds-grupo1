package ecommerce;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Email Service",
                description = "OpenAPI documentation for the Inventory Service."
        )
)
public class email {
    public static void main(String[] args) {
        SpringApplication.run(email.class, args);
    }
}