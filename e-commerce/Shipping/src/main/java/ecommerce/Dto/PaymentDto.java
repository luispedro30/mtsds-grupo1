package ecommerce.Dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PaymentDto {

    private Integer paymentId;
    private Integer orderId;
    private Integer userId;
}
