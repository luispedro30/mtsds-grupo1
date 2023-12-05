package ecommerce;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;


@SpringBootApplication
public class Products {
    public static void main(String[] args) {
        SpringApplication.run(Products.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx){
        return args -> {
            System.out.println("Beans provided by Spring");
            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName: beanNames){
                System.out.println(beanNames);
            }
        };
    }
}