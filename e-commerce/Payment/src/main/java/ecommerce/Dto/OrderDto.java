package ecommerce.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDto {
    private Integer orderId;
    private List products;
    private double priceTotal;

    public OrderDto(int i, ArrayList<ProductDto> productsOfOrder1, double v) {
    }
}
