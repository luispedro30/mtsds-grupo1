package ecommerce.Infra;

import ecommerce.Exceptions.AlreadyExistingException;
import ecommerce.Exceptions.ItemDoesNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AlreadyExistingException.class)
    public ResponseEntity<String> alreadyExistingExceptionHandler(AlreadyExistingException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order already exists");
    }

    @ExceptionHandler(ItemDoesNotExistException.class)
    public ResponseEntity<String> itemDoesNotExistException(ItemDoesNotExistException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
    }


}
