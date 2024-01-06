package ecommerce.Exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WalletNotFoundException extends Exception{

    final String origin, item;

    @Override
    public String getMessage(){
        return String.format("Wallet %s not found %s);",item,origin);
    }

}
