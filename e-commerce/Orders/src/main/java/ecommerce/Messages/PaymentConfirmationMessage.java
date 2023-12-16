package ecommerce.Messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentConfirmationMessage {
    private Integer paymentId;

    private Integer orderId;

    //1 order can only have 1 user
    private Integer userId;

    private double priceTotal;

    private Date createdAt;

    private Date updatedAt;
}
