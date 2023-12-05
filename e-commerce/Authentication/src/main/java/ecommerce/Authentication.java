package ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class Authentication {
    public static void main(String[] args) {
        SpringApplication.run(Authentication.class, args);
        System.out.println("Hello world!");
    }
}