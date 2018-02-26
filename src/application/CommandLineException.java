package application;

/**
 *
 * @author diaz
 */
public class CommandLineException extends Exception {

    public CommandLineException(String message) {
        super(message);
    }
    public CommandLineException(String message, Throwable cause) {
        super(message, cause);
    }
}
