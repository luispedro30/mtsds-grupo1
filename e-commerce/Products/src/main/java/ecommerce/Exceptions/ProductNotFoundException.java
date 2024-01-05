package ecommerce.Exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductNotFoundException extends Exception{

    final String origin, item;

    @Override
    public String getMessage(){
        return String.format("Product %s not found %s);",item,origin);
    }

}
