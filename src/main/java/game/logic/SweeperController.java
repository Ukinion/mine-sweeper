package game.logic;

import game.exception.GameException;
import game.model.MineSweeper;
import javafx.util.Pair;

public class SweeperController
{
    public static final String COORDINATE_X = "x";
    public static final String COORDINATE_Y = "y";
    public static final String PLAYER_NAME = "name";
    public static final String PLAYER_SCORE = "score";
    public static final String PLAYER = "player";

    private static final int EXIT_FAILURE = -1;
    private MineSweeper _gameModel;

    public SweeperController()
    {
        _gameModel = new MineSweeper();
    }

    public void startMineSweeper(int fieldRow, int fieldCol, int numMinesOnField) throws GameException
    {
        _gameModel.startGame(fieldRow, fieldCol, numMinesOnField);
    }

    public void closeMineSweeper()
    {
        _gameModel.closeGame();
    }

    @SuppressWarnings("unchecked")
    public void processPlayerAction(PlayerAction playerAction) throws GameException
    {
        switch(playerAction.getActionType())
        {
            case CLICK ->
            {
                _gameModel.clickOnCell((int)playerAction.getActionParameters(COORDINATE_X),
                        (int)playerAction.getActionParameters(COORDINATE_Y));
            }
            case FLAG ->
            {
                _gameModel.setFlag((int)playerAction.getActionParameters(COORDINATE_X),
                        (int)playerAction.getActionParameters(COORDINATE_Y));
            }
            case EXIT_TO_MENU ->
            {
                _gameModel.closeGame();
            }
            case SAVE_SCORE ->
            {
                _gameModel.getScoreTable().updateScoreTable((String)playerAction.getActionParameters(PLAYER_NAME),
                        (Double)playerAction.getActionParameters(PLAYER_SCORE));
            }
            case REMOVE_SCORE ->
            {
                _gameModel.getScoreTable().removeFromScoreTable((Pair<String, Double>)
                        playerAction.getActionParameters(PLAYER));
            }
            case SERIALIZE_SCORE ->
            {
                _gameModel.getScoreTable().serializeScoreTable();
            }
            case INVALID ->
            {
                System.err.println("Invalid player action! Exit failure...");
                System.exit(EXIT_FAILURE);
            }
        }
    }

}
