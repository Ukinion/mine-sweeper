package game.exception;

public class FileClearErrorException  extends GameException {
    public FileClearErrorException() {
        super("FileError: file has not been cleared!");
    }
}
