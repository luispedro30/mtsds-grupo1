package ecommerce.Infra;

import ecommerce.Exceptions.AlreadyExistingException;
import ecommerce.Exceptions.LoginException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AlreadyExistingException.class)
    public ResponseEntity<String> alreadyExistingExceptionHandler(AlreadyExistingException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User already exists");
    }

    @ExceptionHandler(LoginException.class)
    public ResponseEntity<String> loginException(LoginException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or password invalid");
    }

}
