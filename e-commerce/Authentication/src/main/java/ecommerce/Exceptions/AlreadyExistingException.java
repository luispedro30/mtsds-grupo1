package ecommerce.Exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AlreadyExistingException extends Exception{

    final String origin, item;

    @Override
    public String getMessage(){
        return String.format("User %s already exists (exception thrown by %s);",item,origin);
    }

}
