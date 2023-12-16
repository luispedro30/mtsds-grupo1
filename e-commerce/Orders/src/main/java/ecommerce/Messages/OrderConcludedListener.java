package ecommerce.Messages;

import ecommerce.Models.Products;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;
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
