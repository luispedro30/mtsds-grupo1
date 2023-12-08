package ecommerce.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletDto {
    private Integer id;
    private Integer userId;
    private String value;
}