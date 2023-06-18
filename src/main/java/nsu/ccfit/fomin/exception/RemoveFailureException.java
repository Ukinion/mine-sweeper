package nsu.ccfit.fomin.exception;

public class RemoveFailureException extends GameException {
    public RemoveFailureException() {
        super("\tScoreTableError: specified player does not exist. \n\tTry again: ");
    }
}
