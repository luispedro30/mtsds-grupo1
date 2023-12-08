package ecommerce.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderDto {
    private Integer orderId;
    private List products;
    private double priceTotal;
}
