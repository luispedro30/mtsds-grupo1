package ecommerce.Infra;

import ecommerce.Exceptions.*;
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

    @ExceptionHandler(ProductQuantityException.class)
    public ResponseEntity<String> productQuantityException(ProductQuantityException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Products quantity cannot be lower than 0");
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<String> categoryNotFoundExceptionHandler(CategoryNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Products with request Category not found");
    }

    @ExceptionHandler(NameNotFoundException.class)
    public ResponseEntity<String> nameNotFoundExceptionHandler(NameNotFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Products with the request name not found");
    }
}
