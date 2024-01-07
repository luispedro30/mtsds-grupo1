package ecommerce.Exceptions;

public class NoShippingFoundException extends RuntimeException {

    public NoShippingFoundException(String message) {
        super(message);
    }

    public NoShippingFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

