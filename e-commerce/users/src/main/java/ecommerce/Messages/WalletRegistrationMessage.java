package ecommerce.Messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WalletRegistrationMessage {
    private Integer userId;
    private float value;
}
