package cassava.csv.core.exceptions;

/**
 * Created by andrew on 02/11/15.
 */
public class ConversionException extends RuntimeException {

    public ConversionException(String message) {
        super(message);
    }

    public ConversionException(String message, Throwable e) {
        super(message,e);
    }
}
