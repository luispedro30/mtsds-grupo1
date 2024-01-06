package ecommerce.Exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NameNotFoundException extends Exception{

    final String origin, item;

    @Override
    public String getMessage(){
        return String.format("Product with Name %s not found %s);",item,origin);
    }

}
