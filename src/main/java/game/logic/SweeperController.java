package game.logic;

import game.exception.GameException;
import game.model.MineSweeper;

import static game.display.Console.IGNORE_PARAMETERS;

public class SweeperController
{
    public static final String COORDINATE_X = "x";
    public static final String COORDINATE_Y = "y";
    public static final String PLAYER_NAME = "name";
    public static final String PLAYER_SCORE = "score";
    public static final String PLAYER = "player";
    public static final String EXIT_FROM_GAME = "exit";
    public static final String INVALID_ACTION = "invalid";
    public static final int EXIT_FAILURE = -1;

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
                resetAction(playerAction);
                _gameModel.clickOnCell((int)playerAction.getActionParameters(COORDINATE_X),
                        (int)playerAction.getActionParameters(COORDINATE_Y));
            }
            case FLAG ->
            {
                resetAction(playerAction);
                _gameModel.useFlag((int)playerAction.getActionParameters(COORDINATE_X),
                        (int)playerAction.getActionParameters(COORDINATE_Y));
            }
            case EXIT ->
            {
                resetAction(playerAction);
                _gameModel.closeGame();

            }
            case SAVE_SCORE ->
            {
                resetAction(playerAction);
                _gameModel.getScoreTable().updateScoreTable((String)playerAction.getActionParameters(PLAYER_NAME),
                        (Double)playerAction.getActionParameters(PLAYER_SCORE));
            }
            case REMOVE_SCORE ->
            {
                resetAction(playerAction);
                _gameModel.getScoreTable().removeFromScoreTable((String)
                        playerAction.getActionParameters(PLAYER));
            }
            case SERIALIZE_SCORE ->
            {
                resetAction(playerAction);
                _gameModel.getScoreTable().serializeScoreTable();
            }
            case INVALID ->
            {
                System.err.println("Invalid player action! Exit failure...");
                System.exit(EXIT_FAILURE);
            }
        }
    }

    private void resetAction(PlayerAction action)
    {
        action.defineAction(SweeperController.INVALID_ACTION,
                IGNORE_PARAMETERS, PlayerAction.ActionType.INVALID);
    }


    public MineSweeper getGameModel()
    {
        return _gameModel;
    }

}
