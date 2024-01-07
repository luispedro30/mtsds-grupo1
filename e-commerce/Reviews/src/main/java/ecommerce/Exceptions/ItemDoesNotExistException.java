package ecommerce.Exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ItemDoesNotExistException extends Exception{

    final String origin,item;

    @Override
    public String getMessage(){
        return String.format("Item %s does not exist (exception thrown by %s", item, origin);
    }
}
