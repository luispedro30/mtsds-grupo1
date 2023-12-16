package ecommerce.Dto;

import ecommerce.Enum.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ShippingDto {
    private Integer shippingId;

    private Integer paymentId;

    private Integer orderId;

    //1 order can only have 1 user
    private Integer userId;

    private Status status;
}
