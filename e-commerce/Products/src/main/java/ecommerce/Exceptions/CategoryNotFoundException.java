package ecommerce.Exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CategoryNotFoundException extends Exception{

    final String origin, item;

    @Override
    public String getMessage(){
        return String.format("Product with Category %s not found %s);",item,origin);
    }

}
