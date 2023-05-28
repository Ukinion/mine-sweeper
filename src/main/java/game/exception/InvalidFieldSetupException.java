package game.exception;

public class InvalidFieldSetupException extends GameException {
    public InvalidFieldSetupException() {
        super("GameFieldError: invalid data for creating field");
    }

    public InvalidFieldSetupException(String message) {
        super(message);
    }
}
