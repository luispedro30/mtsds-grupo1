package ecommerce.Exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginException extends Exception{

    final String origin;

    @Override
    public String getMessage(){
        return String.format("User or password does not exist (exception thrown by %s);",origin);
    }

}
