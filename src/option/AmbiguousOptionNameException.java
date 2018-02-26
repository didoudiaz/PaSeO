package option;

/**
 *
 * @author diaz
 */
public class AmbiguousOptionNameException extends Exception {

    public AmbiguousOptionNameException(String message) {
        super(message);
    }
    public AmbiguousOptionNameException(String message, Throwable cause) {
        super(message, cause);
    }
}
