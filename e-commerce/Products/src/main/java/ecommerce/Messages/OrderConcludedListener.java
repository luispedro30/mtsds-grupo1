package ecommerce.Messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderConcludedListener {

    private Integer orderId;

    //1 order can only have 1 user
    private Integer userId;

    //1 order can have various products
    private List<Integer> productIds;

}
