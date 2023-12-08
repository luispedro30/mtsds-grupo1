package ecommerce.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShippingDto {
    private Integer paymentId;

    private Integer orderId;

    private Integer userId;

    private String status;
}
