package game.exception;

public class DeserializeFailureException extends GameException
{
    public DeserializeFailureException()
    {
        super("ScoreTableError: failure while deserialize score table");
    }
}
