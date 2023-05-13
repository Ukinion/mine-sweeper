package game.exception;

public class ActionParameterException extends GameException
{
    public ActionParameterException()
    {
        super("ActionParameterError: invalid number of parameters");
    }
}
