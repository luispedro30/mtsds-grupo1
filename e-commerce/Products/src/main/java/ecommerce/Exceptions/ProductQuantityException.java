package ecommerce.Exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductQuantityException extends Exception{

    final String origin, item;

    @Override
    public String getMessage(){
        return String.format("Product quantity of %s cannot be lower than 0 on %s);",item,origin);
    }

}
