package cassava.csv.core.exceptions;

/**
 * @author Andrew Vella
 * @since 02/11/15.
 */
public class ConversionException extends RuntimeException {

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(String message, Throwable e) {
        super(message,e);
    }
}
