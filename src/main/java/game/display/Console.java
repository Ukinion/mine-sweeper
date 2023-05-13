package game.display;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Scanner;

import game.exception.ActionParameterException;
import game.exception.GameException;
import game.exception.InvalidFieldSetupException;
import game.logic.PlayerAction;
import game.logic.SweeperController;
import game.model.MineSweeper;
import game.objects.field.MineField;
import game.objects.unit.Cell;
import javafx.util.Pair;

public class Console implements PropertyChangeListener
{
    private static final String EXIT = "e";
    private static final String FLAG = "f";
    private static final String SCORE = "st";
    private static final String CLICK = "c";
    private static final String BACK = "b";
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

    private static final String INIT_OPTION = "1";
    private static final String RECORDS_OPTION = "2";
    private static final String INFO_OPTION = "3";
    private static final String EXIT_OPTION = "4";

    private static final int MAX_FIELD_SIZE = 99;

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
        initGameObjects();
    }

    public void invokeGameMenuWindow()
    {
        while (true)
        {
            printMenu();
            switch (_consoleScanner.next())
            {
                case INIT_OPTION -> launchGame();
                case RECORDS_OPTION -> openScoreTable();
                case INFO_OPTION -> printGameInfo();
                case EXIT_OPTION -> exitGame();
                default -> System.out.println("\tInvalid menu option. Try again");
            }
        }
    }

    public void printMenu()
    {
        System.out.println("\t\t******Minesweeper menu******");
        System.out.println("\t1. Start new game.");
        System.out.println("\t2. Records.");
        System.out.println("\t3. About.");
        System.out.println("\t4. Exit.");
        System.out.println("\t\t\t********************");
    }

    private void launchGame()
    {
        int fieldRow, fieldCol, numMinesOnField;
        System.out.println("\t\t******New game options******");
        System.out.println("\tEnter game field row: ");
        fieldRow = getInputDigit();
        System.out.println("\tEnter game field col: ");
        fieldCol = getInputDigit();
        System.out.println("\tEnter number of mines: ");
        numMinesOnField = getInputDigit();

        try
        {
            if (fieldCol == MAX_FIELD_SIZE || fieldRow == MAX_FIELD_SIZE)
            {
                throw new InvalidFieldSetupException("Field is too big for Console UI");
            }
            _gameController.startMineSweeper(fieldRow, fieldCol, numMinesOnField);
        }
        catch(GameException e)
        {
            System.out.println(e.getMessage());
            System.out.println("Back to menu.");
            System.out.println("\t\t\t***************");
        }
    }

    private void initGameObjects()
    {
        _gameModel = _gameController.getGameModel();
        _gameField = _gameModel.getGameField();
    }

    private int getInputDigit()
    {
        while (true)
        {
            try
            {
                return Integer.parseInt(_consoleScanner.next());
            }
            catch (NumberFormatException e)
            {
                System.out.println("Incorrect number format! Try again.");
            }
        }
    }

    private void openScoreTable()
    {
        System.out.println("\t\t*******Score table*******");
        var playerList = _gameModel.getScoreTable().getScoreTable();
        StringBuilder tableWindow = new StringBuilder();
        int place = 1;
        for (Pair<String, Double> player : playerList)
        {
            tableWindow.append(place++);
            tableWindow.append(".");
            tableWindow.append(player.getKey());
            tableWindow.append(" - ");
            tableWindow.append(player.getValue());
            System.out.println(tableWindow);
            tableWindow.setLength(EMPTY_STRING);
        }
        System.out.println("\n\t   Write name to remove score.");
        System.out.println("\n\tb. Back.");
        System.out.println("\t\t\t********************");
        waitRespondFromPlayer();
    }

    private void waitRespondFromPlayer()
    {
        String respond;
        while(true)
        {
            respond = _consoleScanner.next();
            if (respond.equals(BACK)) break;
            try
            {
                _playerAction.defineAction(SweeperController.PLAYER,
                        respond, PlayerAction.ActionType.REMOVE_SCORE);
                _gameController.processPlayerAction(_playerAction);
            }
            catch (NumberFormatException | GameException e)
            {
                System.out.println(e.getMessage());
            }
        }
    }

    private void printGameInfo()
    {
        System.out.println("\t\t******Minesweeper about******");
        System.out.println(_gameModel.getAboutInfo());
        System.out.println("\n\tb. Back.");
        System.out.println("\t\t\t********************");
        waitRespondFromPlayer();
    }

    private void exitGame()
    {
        _playerAction.defineAction(IGNORE_PARAMETERS, IGNORE_PARAMETERS,
                PlayerAction.ActionType.SERIALIZE_SCORE);
        try
        {
            _gameController.processPlayerAction(_playerAction);
            pseudoClearConsole();
            System.exit(SweeperController.EXIT_FAILURE);
        }
        catch (GameException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void pseudoClearConsole()
    {
        for(int clear = 0; clear < 500; clear++)
        {
            System.out.println("\b") ;
        }
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
        System.out.print("Write coords to click \"c x y\", \"e\" to exit, " +
                "\"f x y\" for flagging, add yourself to score table(if game over) \"st [name]");
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
        switch(action[FIRST_PARAMETER])
        {
            case EXIT ->
            {
                if (action.length == UNARY_ACTION)
                {
                    _playerAction.defineAction(SweeperController.EXIT_FROM_GAME,
                            EXIT, PlayerAction.ActionType.EXIT);
                }
                else
                {
                    _playerAction.defineAction(SweeperController.INVALID_ACTION,
                            IGNORE_PARAMETERS, PlayerAction.ActionType.INVALID);
                }
            }
            case CLICK ->
            {
                try
                {
                    if (action.length != BINARY_ACTION) throw new ActionParameterException();
                    _playerAction.defineAction(SweeperController.COORDINATE_X,
                            Integer.parseInt(action[FIRST_PARAMETER]), PlayerAction.ActionType.CLICK);
                    _playerAction.defineAction(SweeperController.COORDINATE_Y,
                            Integer.parseInt(action[SECOND_PARAMETER]), PlayerAction.ActionType.CLICK);
                }
                catch (NumberFormatException | ActionParameterException ex)
                {
                    System.out.println("Action cant be executed! Please, try again.");
                    _playerAction.defineAction(SweeperController.INVALID_ACTION,
                            IGNORE_PARAMETERS, PlayerAction.ActionType.INVALID);
                }
            }
            case FLAG ->
            {
                try
                {
                    if (action.length == TERNARY_ACTION) throw new ActionParameterException();
                    _playerAction.defineAction(SweeperController.COORDINATE_X,
                            Integer.parseInt(action[SECOND_PARAMETER]), PlayerAction.ActionType.FLAG);
                    _playerAction.defineAction(SweeperController.COORDINATE_Y,
                            Integer.parseInt(action[THIRD_PARAMETER]), PlayerAction.ActionType.FLAG);
                }
                catch (NumberFormatException | ActionParameterException ex)
                {
                    System.out.println("Action cant be executed! Please, try again.");
                    _playerAction.defineAction(SweeperController.INVALID_ACTION,
                            IGNORE_PARAMETERS, PlayerAction.ActionType.INVALID);
                }
            }
            case SCORE ->
            {
                if (action.length == BINARY_ACTION)
                {
                    _gameModel.resetGameTimer();
                    _playerAction.defineAction(SweeperController.PLAYER_NAME,
                            action[SECOND_PARAMETER], PlayerAction.ActionType.SAVE_SCORE);
                    _playerAction.defineAction(SweeperController.PLAYER_SCORE,
                            _gameModel.getCurScore(), PlayerAction.ActionType.SAVE_SCORE);
                }
                else
                {
                    System.out.println("Action cant be executed! Please, try again.");
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
            case ACTION -> printGameField();
            case DEFEAT ->
            {
                System.out.println("DEFEAT!");
                printGameField();
            }
            case VICTORY ->
            {
                System.out.println("VICTORY!");
                printGameField();
            }
            case CLOSED -> System.out.println("Back to menu.");
        }
        requestForPlayerAction();
    }
}
