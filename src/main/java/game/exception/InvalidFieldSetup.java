package game.exception;

public class InvalidFieldSetup extends GameException
{
    public InvalidFieldSetup()
    {
        super("GameFieldError: invalid data for creating field\n");
    }
}
