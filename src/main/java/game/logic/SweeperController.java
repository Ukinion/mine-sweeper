package game.logic;

import game.exception.GameException;
import game.model.MineSweeper;

import static game.display.Console.IGNORE_PARAMETERS;

public class SweeperController {
    public static final String COORDINATE_X = "x";
    public static final String COORDINATE_Y = "y";
    public static final String PLAYER_NAME = "name";
    public static final String PLAYER_SCORE = "score";
    public static final String PLAYER = "player";
    public static final String EXIT_FROM_GAME = "exit";
    public static final String INVALID_ACTION = "invalid";
    public static final int EXIT_FAILURE = -1;

    private MineSweeper _gameModel;

    public SweeperController() {
        _gameModel = new MineSweeper();
    }

    public void initGameObjects(int fieldRow, int fieldCol, int numMinesOnField) throws GameException {
        _gameModel.initGameObjects(fieldRow, fieldCol, numMinesOnField);
    }

    public void startMineSweeper() {
        _gameModel.startGame();
    }

    @SuppressWarnings("unchecked")
    public void processPlayerAction(PlayerAction playerAction) throws GameException {
        switch(playerAction.getActionType()) {
            case CLICK -> {
                resetAction(playerAction);
                _gameModel.clickOnCell((int)playerAction.getActionParameters(COORDINATE_X),
                        (int)playerAction.getActionParameters(COORDINATE_Y));
            }
            case FLAG -> {
                resetAction(playerAction);
                _gameModel.useFlag((int)playerAction.getActionParameters(COORDINATE_X),
                        (int)playerAction.getActionParameters(COORDINATE_Y));
            }
            case EXIT -> {
                resetAction(playerAction);
                _gameModel.forceCloseGame();
            }
            case SAVE_SCORE -> {
                resetAction(playerAction);
                _gameModel.getScoreTable().updateScoreTable((String)playerAction.getActionParameters(PLAYER_NAME),
                        (Integer)playerAction.getActionParameters(PLAYER_SCORE));
            }
            case REMOVE_SCORE -> {
                resetAction(playerAction);
                _gameModel.getScoreTable().removeFromScoreTable((String)
                        playerAction.getActionParameters(PLAYER));
            }
            case SERIALIZE_SCORE -> {
                resetAction(playerAction);
                _gameModel.getScoreTable().serializeScoreTable();
            }
            case INVALID -> {
                System.err.println("Invalid player action! Exit failure...");
                System.exit(EXIT_FAILURE);
            }
        }
    }

    private void resetAction(PlayerAction action) {
        action.defineAction(SweeperController.INVALID_ACTION,
                IGNORE_PARAMETERS, PlayerAction.ActionType.INVALID);
    }

    public MineSweeper getGameModel() {
        return _gameModel;
    }
}
