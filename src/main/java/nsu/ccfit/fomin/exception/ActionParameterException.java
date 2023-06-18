package nsu.ccfit.fomin.exception;

public class ActionParameterException extends GameException {
    public ActionParameterException() {
        super("ActionParameterError: invalid number of parameters");
    }
}
