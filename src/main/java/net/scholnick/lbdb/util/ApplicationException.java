package net.scholnick.lbdb.util;

/**
 * ApplicationException is a runtime exception that indicates an application-level error.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
public class ApplicationException extends RuntimeException {
    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }
}
