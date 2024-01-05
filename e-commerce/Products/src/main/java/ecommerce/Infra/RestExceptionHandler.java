package ecommerce.Infra;

import ecommerce.Exceptions.AlreadyExistingException;
import ecommerce.Exceptions.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AlreadyExistingException.class)
    public ResponseEntity<String> alreadyExistingExceptionHandler(AlreadyExistingException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product already exists");
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> productNotFoundExceptionHandler(ProductNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Products not found");
    }
}
