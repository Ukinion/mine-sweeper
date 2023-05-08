package game.display;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Scanner;
import java.util.SimpleTimeZone;

import game.exception.GameException;
import game.logic.PlayerAction;
import game.logic.SweeperController;
import game.model.MineSweeper;
import game.objects.field.MineField;
import game.objects.unit.Cell;


public class Console implements PropertyChangeListener
{
    private static final String EXIT = "e";
    private static final String FLAG = "F";
    private static final String IGNORE_PARAMETERS = "i";
    private static final String DELIMITER_ACTION_PARAMETERS = " ";

    private static final int UNARY_ACTION = 1;
    private static final int BINARY_ACTION = 2;
    private static final int TERNARY_ACTION = 3;
    private static final int FIRST_PARAMETER = 0;
    private static final int SECOND_PARAMETER = 1;
    private static final int THIRD_PARAMETER = 2;

    private static final int TWO_DIGIT_NUMBER = 2;
    private static final int EMPTY_STRING = 0;

    private static final String RESET_COLOR = "\u001B[0m";
    private static final String RED_COLOR = "\u001b[31m";
    private static final String GREEN_COLOR = "\u001b[32m";
    private static final String YELLOW_COLOR = "\u001b[33m";
    private static final String BLUE_COLOR = "\u001b[34m";
    private static final String MAGENTA_COLOR = "\u001b[35m";
    private static final String CYAN_COLOR = "\u001b[36m";
    private static final String WHITE_COLOR = "\u001b[37m";

    private static final String UNKNOWN_CELL = " ";
    private static final String MINE_CELL = "*";

    private PlayerAction _playerAction;
    private SweeperController _gameController;
    private MineSweeper _gameModel;
    private MineField _gameField;
    private Scanner _consoleScanner;

    public Console()
    {
        _consoleScanner = new Scanner(System.in);
        _playerAction = new PlayerAction();
        _gameController = new SweeperController();
        _gameModel = _gameController.getGameModel();
        _gameField = _gameModel.getGameField();
    }

    @Override
    public void propertyChange(PropertyChangeEvent gameEvent)
    {
        switch (gameEvent.getPropertyName())
        {
            case MineSweeper.IGNORE_EVENT -> {}
            case MineSweeper.REQUEST_ACTION_EVENT -> requestForPlayerAction();
            case MineSweeper.FIELD_CHANGE_EVENT ->
            {
                printGameField();
                printTimeAndScore();
                requestForPlayerAction();
            }
            case MineSweeper.CHANGE_GAME_STAGE_EVENT ->
                    respondToNewStage((MineSweeper.GameStage) gameEvent.getNewValue());
        }
    }

    private void requestForPlayerAction()
    {
        System.out.print("Write coords to click \"x y\", \"e\" to exit, " +
                "\"f x y\" for flagging, \"st [name] ");
        while(_playerAction.isInvalidAction())
        {
            defineAction(_consoleScanner.nextLine().split(DELIMITER_ACTION_PARAMETERS));
        }
        try
        {
            _gameController.processPlayerAction(_playerAction);
        }
        catch (GameException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void defineAction(String[] action)
    {
        switch(action.length)
        {
            case UNARY_ACTION ->
            {
                if (action[FIRST_PARAMETER].equals(EXIT))
                {
                    _playerAction.defineAction(SweeperController.EXIT_FROM_GAME,
                            EXIT, PlayerAction.ActionType.EXIT_TO_MENU);
                }
                else
                {
                    _playerAction.defineAction(SweeperController.INVALID_ACTION,
                            IGNORE_PARAMETERS, PlayerAction.ActionType.INVALID);
                }
            }
            case BINARY_ACTION ->
            {
                try
                {
                    _playerAction.defineAction(SweeperController.COORDINATE_X,
                            Integer.parseInt(action[FIRST_PARAMETER]), PlayerAction.ActionType.CLICK);
                    _playerAction.defineAction(SweeperController.COORDINATE_Y,
                            Integer.parseInt(action[SECOND_PARAMETER]), PlayerAction.ActionType.CLICK);
                }
                catch (NumberFormatException ex)
                {
                    System.out.println("Incorrect coordinates! Please, try again.");
                    _playerAction.defineAction(SweeperController.INVALID_ACTION,
                            IGNORE_PARAMETERS, PlayerAction.ActionType.INVALID);
                }
            }
            case TERNARY_ACTION ->
            {
                if (action[FIRST_PARAMETER].equals(FLAG))
                {
                    try
                    {
                        _playerAction.defineAction(SweeperController.COORDINATE_X,
                                Integer.parseInt(action[SECOND_PARAMETER]), PlayerAction.ActionType.FLAG);
                        _playerAction.defineAction(SweeperController.COORDINATE_Y,
                                Integer.parseInt(action[THIRD_PARAMETER]), PlayerAction.ActionType.FLAG);
                    }
                    catch (NumberFormatException ex)
                    {
                        System.out.println("Incorrect coordinates! Please, try again.");
                        _playerAction.defineAction(SweeperController.INVALID_ACTION,
                                IGNORE_PARAMETERS, PlayerAction.ActionType.INVALID);
                    }
                }
                else
                {
                    _playerAction.defineAction(SweeperController.INVALID_ACTION,
                            IGNORE_PARAMETERS, PlayerAction.ActionType.INVALID);
                }
            }
            default -> _playerAction.defineAction(SweeperController.INVALID_ACTION,
                    IGNORE_PARAMETERS, PlayerAction.ActionType.INVALID);
        }
    }

    private void printGameField()
    {
        StringBuilder gameWindow = new StringBuilder();
        gameWindow.append("   ");
        for (var i = 0; i < _gameField.getFieldCol(); ++i)
        {
            gameWindow.append(printWindowBoard(i));
        }
        System.out.println(gameWindow);

        for (var i = 0; i < _gameField.getFieldRow(); ++i)
        {
            gameWindow.setLength(EMPTY_STRING);
            gameWindow.append(RESET_COLOR);
            gameWindow.append(printWindowBoard(i));
            for (var j = 0; j < _gameField.getFieldCol(); ++j)
            {
                printCell(_gameField.locateCell(j, i), gameWindow);
            }
        }
    }

    private String printWindowBoard(int num)
    {
        String str = Integer.toString(num);
        if (str.length() == TWO_DIGIT_NUMBER)
        {
            str += "  ";
        }
        else
        {
            str += " ";
        }
        return str;
    }

    private void printCell(Cell cell, StringBuilder gameWindow)
    {
        if (cell.isOpened())
        {
            gameWindow.append(defineColorForCell(cell));
            if (cell.isMine())
            {
                gameWindow.append(MINE_CELL);
            }
            else
            {
                gameWindow.append(cell.getMinesAround());
            }
        }
        else
        {
            if (cell.isFlag())
            {
                gameWindow.append(FLAG);
            }
            else
            {
                gameWindow.append(UNKNOWN_CELL);
            }
        }
    }

    private String defineColorForCell(Cell cell)
    {
        if (cell.isMine()) { return RED_COLOR; }
        switch (cell.getMinesAround())
        {
            case 0 -> { return WHITE_COLOR; }
            case 1 -> { return GREEN_COLOR; }
            case 2 -> { return CYAN_COLOR; }
            case 3 -> { return BLUE_COLOR; }
            case 4 -> { return YELLOW_COLOR; }
            case 5 -> { return MAGENTA_COLOR; }
            default -> { return RED_COLOR; }
        }
    }

    private void printTimeAndScore()
    {
        System.out.println("Time: " + _gameModel.getCurGameTime()
                + "\tScore: " + _gameModel.getCurScore());
    }

    private void respondToNewStage(MineSweeper.GameStage newStage)
    {
        switch (newStage)
        {
            case ACTION ->
            {
                printGameField();
                requestForPlayerAction();
            }
            case DEFEAT ->
            {
                System.out.println("DEFEAT!");
                printGameField();
            }
            case VICTORY ->
            {
                System.out.println("VICTORY!");
                printGameField();
                requestForPlayerAction();
            }
        }
    }
}
