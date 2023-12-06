package ecommerce.Dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.List;

@Getter
@Setter
public class OrderDto {
    private Integer orderId;
    private List products;
}
