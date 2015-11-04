package cassava.csv.core.exceptions;

/**
 * @author Andrew Vella
 * @since 04/11/15.
 */
public class ConfigurationException extends RuntimeException {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
