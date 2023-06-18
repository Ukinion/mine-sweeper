package nsu.ccfit.fomin.exception;

public class SerializeFailureException extends GameException {
    public SerializeFailureException() {
        super("ScoreTableError: failure while serialize score table");
    }
}
