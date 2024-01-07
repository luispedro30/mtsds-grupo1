package ecommerce.Exceptions;

public class NoReviewsFoundException extends RuntimeException {

    public NoReviewsFoundException(String message) {
        super(message);
    }

    public NoReviewsFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

