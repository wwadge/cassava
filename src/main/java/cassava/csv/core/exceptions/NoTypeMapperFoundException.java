package cassava.csv.core.exceptions;

/**
 * @author Andrew Vella
 * @since 11/12/15.
 */
public class NoTypeMapperFoundException extends RuntimeException {
    public NoTypeMapperFoundException(String message) {
        super(message);
    }

    public NoTypeMapperFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
