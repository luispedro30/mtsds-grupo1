package ecommerce.Infra;

import ecommerce.Exceptions.AlreadyExistingException;
import ecommerce.Exceptions.WalletNotFoundException;
import ecommerce.Exceptions.WalletValueException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AlreadyExistingException.class)
    public ResponseEntity<String> alreadyExistingExceptionHandler(AlreadyExistingException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Wallet already exists");
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<String> walletNotFoundException(WalletNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Wallet does not exist");
    }

    @ExceptionHandler(WalletValueException.class)
    public ResponseEntity<String> walletValueException(WalletValueException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Wallet money cannot be lower than 0");
    }
}

