package ecommerce.Infra;

import ecommerce.Exceptions.AlreadyExistingException;
import ecommerce.Exceptions.CustomInternalServerException;
import ecommerce.Exceptions.ItemDoesNotExistException;
import ecommerce.Exceptions.NoShippingFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AlreadyExistingException.class)
    public ResponseEntity<String> alreadyExistingExceptionHandler(AlreadyExistingException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shipping already exists");
    }

    @ExceptionHandler(ItemDoesNotExistException.class)
    public ResponseEntity<String> itemDoesNotExistException(ItemDoesNotExistException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shipping not found");
    }

    @ExceptionHandler(NoShippingFoundException.class)
    public ResponseEntity<String> noReviewsFoundException(NoShippingFoundException exception){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shippings not found");
    }

    @ExceptionHandler(CustomInternalServerException.class)
    public ResponseEntity<String> customInternalServerExceptionHandler(CustomInternalServerException exception){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exception.getMessage());
    }



}
