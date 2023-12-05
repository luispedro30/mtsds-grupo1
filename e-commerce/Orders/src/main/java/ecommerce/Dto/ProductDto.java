package ecommerce.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto {
    private Integer id;

    private String name;

    private String description;

    private String category;

    private double price;

    private Integer stockQuantity;
}
