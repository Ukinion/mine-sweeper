package game.display;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
    private static final String EXIT = "3";
    private static final String FLAG = "2";
    private static final String CLICK_OR_SAVE_SCORE = "1";
    private static final String RETURN = "r";
    public static final String IGNORE_PARAMETERS = "i";
    private static final String DELIMITER_ACTION_PARAMETERS = " ";

    private static final String MINE_IMAGE = "B";
    private static final String FLAG_IMAGE = "F";
    private static final String CLOSED_CELL_IMAGE = "*";

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
        initGame();
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
            System.out.println("Return to menu.");
            System.out.println("\t\t\t***************");
        }
    }

    private void initGame()
    {
        _gameModel = _gameController.getGameModel();
        _gameModel.addListener(this);
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
                System.out.println("\n\tIncorrect number format! Try again:");
            }
        }
    }

    private void openScoreTable()
    {
        System.out.println("\t\t*******Score table*******");
        var playerList = _gameModel.getScoreTable().getScoreTable();
        if (playerList.isEmpty())
        {
            System.out.println("\tTable is empty. You can be first at the top!");
            System.out.println("\t\t\t********************");
            System.out.println("\n\tPress r to return to the menu.");
            waitRespondFromPlayer();
            return;
        }
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
        System.out.println("\n\t  Write name to remove score.");
        System.out.println("\n\tPress r to return to the menu.");
        System.out.println("\t\t\t********************");
        waitRespondFromPlayer();
    }

    private void waitRespondFromPlayer()
    {
        String respond;
        while(true)
        {
            respond = _consoleScanner.next();
            if (respond.equals(RETURN)) break;
            try
            {
                if (_gameModel.isVictoryStage())
                {
                    _playerAction.defineAction(SweeperController.PLAYER,
                            respond, PlayerAction.ActionType.REMOVE_SCORE);
                    _gameController.processPlayerAction(_playerAction);
                    break;
                }
                else System.out.println("\n\tPress r to return to the menu.");
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
        System.out.println("\t\t\t********************");
        System.out.println("\n\tPress r to return to the menu.");
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
            {
                if (_gameField == null) { _gameField = _gameModel.getGameField(); }
                respondToNewStage((MineSweeper.GameStage) gameEvent.getNewValue());
            }
        }
    }

    private void requestForPlayerAction()
    {
        try
        {
            showAvailableActionList();
            while(_playerAction.isInvalidAction())
            {
                defineAction(_consoleScanner.nextLine().split(DELIMITER_ACTION_PARAMETERS));
            }
            _gameController.processPlayerAction(_playerAction);

        }
        catch (GameException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void showAvailableActionList()
    {
        System.out.println("\t\t******Action List******");
        if (_gameModel.isVictoryStage())
        {
            System.out.println("\t1. Save score \"[yourname].\"");
            System.out.println("\t2. End game and exit to menu.");
        }
        else
        {
            System.out.println("\t1. Write coords to open cell \"[coordinate] [coordinate]\".");
            System.out.println("\t2. Setup flag to mark mine \"[coordinate] [coordinate]\".");
            System.out.println("\t3. End game and exit to menu.");
        }
        System.out.println("\t\t****************");
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
            case CLICK_OR_SAVE_SCORE ->
            {
                try
                {
                    if (action.length == TERNARY_ACTION)
                    {
                        _playerAction.defineAction(SweeperController.COORDINATE_X,
                                Integer.parseInt(action[FIRST_PARAMETER]), PlayerAction.ActionType.CLICK);
                        _playerAction.defineAction(SweeperController.COORDINATE_Y,
                                Integer.parseInt(action[SECOND_PARAMETER]), PlayerAction.ActionType.CLICK);
                    }
                    else if (action.length == BINARY_ACTION && _gameModel.isVictoryStage())
                    {
                        _gameModel.resetGameTimer();
                        _playerAction.defineAction(SweeperController.PLAYER_NAME,
                                action[FIRST_PARAMETER], PlayerAction.ActionType.SAVE_SCORE);
                        _playerAction.defineAction(SweeperController.PLAYER_SCORE,
                                _gameModel.getCurScore(), PlayerAction.ActionType.SAVE_SCORE);
                    }
                    else throw new ActionParameterException();
                }
                catch (NumberFormatException | ActionParameterException ex)
                {
                    System.out.println("\tAction cant be executed! Please, try again.");
                    _playerAction.defineAction(SweeperController.INVALID_ACTION,
                            IGNORE_PARAMETERS, PlayerAction.ActionType.INVALID);
                }
            }
            case FLAG ->
            {
                try
                {
                    if (action.length != TERNARY_ACTION) throw new ActionParameterException();
                    _playerAction.defineAction(SweeperController.COORDINATE_X,
                            Integer.parseInt(action[SECOND_PARAMETER]), PlayerAction.ActionType.FLAG);
                    _playerAction.defineAction(SweeperController.COORDINATE_Y,
                            Integer.parseInt(action[THIRD_PARAMETER]), PlayerAction.ActionType.FLAG);
                }
                catch (NumberFormatException | ActionParameterException ex)
                {
                    System.out.println("\tAction cant be executed! Please, try again.");
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
                gameWindow.append(printSpaces(j));
                printCell(_gameField.locateCell(j, i), gameWindow);
            }
            System.out.println(gameWindow);
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

    private String printSpaces(int j)
    {
        if (Integer.toString(j).length() == TWO_DIGIT_NUMBER) return "  ";
        else return " ";
    }

    private void printCell(Cell cell, StringBuilder gameWindow)
    {
        if (cell.isOpened())
        {
            gameWindow.append(defineColorForCell(cell));
            if (cell.isMine())
            {
                gameWindow.append(RESET_COLOR);
                gameWindow.append(MINE_IMAGE);
            }
            else if (cell.isFlag())
            {
                gameWindow.append(FLAG_IMAGE);
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
                gameWindow.append(RESET_COLOR);
                gameWindow.append(FLAG_IMAGE);
            }
            else
            {
                gameWindow.append(CLOSED_CELL_IMAGE);
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
            case CLOSED ->
            {
                return;
            }
        }
        requestForPlayerAction();
    }
}
