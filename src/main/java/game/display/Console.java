package game.display;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Scanner;

import game.exception.ActionParameterException;
import game.exception.GameException;
import game.logic.PlayerAction;
import game.logic.SweeperController;
import game.model.MineSweeper;
import game.objects.field.MineField;
import game.objects.unit.Cell;
import javafx.util.Pair;

public class Console implements PropertyChangeListener {
    private static final String EXIT = "e";
    private static final String FLAG = "f";
    private static final String CLICK = "w";
    private static final String SCORE = "s";
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

    private static final int GODLIKE_RANK = 1;
    private static final int DIVINE_RANK = 2;
    private static final int CHAMPION_RANK = 3;
    private static final int KNIGHT_RANK = 4;

    private static final String GODLIKE = "Godlike";
    private static final String DIVINE = "Divine";
    private static final String CHAMPION = "Champion";
    private static final String KNIGHT = "Knight";
    private static final String Loon  = "Loon";

    private PlayerAction _playerAction;
    private SweeperController _gameController;
    private MineSweeper _gameModel;
    private MineField _gameField;
    private Scanner _consoleScanner;

    public Console() {
        _consoleScanner = new Scanner(System.in);
        _playerAction = new PlayerAction();
        _gameController = new SweeperController();
        _gameModel = _gameController.getGameModel();
    }

    public void invokeGameMenuWindow() {
        while (true) {
            printMenu();
            switch (_consoleScanner.next()) {
                case INIT_OPTION -> {
                    initGame();
                    launchGame();
                }
                case RECORDS_OPTION ->  {
                    openScoreTable();
                    removePlayerRequest();
                }
                case INFO_OPTION -> printGameInfo();
                case EXIT_OPTION -> exitGame();
                default -> System.out.println("\tInvalid menu option. Try again.");
            }
        }
    }

    public void printMenu() {
        System.out.println("\n\n\n\t******Minesweeper menu******");
        System.out.println("\t1. Start new game.");
        System.out.println("\t2. Records.");
        System.out.println("\t3. About.");
        System.out.println("\t4. Exit.");
        System.out.println("\t****************************");
    }

    private void initGame() {
        int fieldRow, fieldCol, numMinesOnField;
        System.out.println("\n\n\n\t******New game options******");
        System.out.println("\tEnter game field row 1-27:");
        fieldRow = getInputDigit();
        System.out.println("\tEnter game field col 1-27:");
        fieldCol = getInputDigit();
        System.out.println("\tEnter number of mines:");
        numMinesOnField = getInputDigit();
        System.out.println("\t****************************\n");
        try {
            _gameController.initGameObjects(fieldRow, fieldCol, numMinesOnField);
            _gameModel = _gameController.getGameModel();
            _gameModel.addListener(this);
            _gameField = _gameModel.getGameField();
        } catch(GameException e) {
            System.out.print("\tWarning!\n\t");
            System.out.println(e.getMessage());
            System.out.println("\n\tPress r to return to the menu.");
            listenReturnButton();
            invokeGameMenuWindow();
        }
    }

    private void launchGame() {
        _gameController.startMineSweeper();
    }

    private int getInputDigit() {
        while (true) {
            try {
                return Integer.parseInt(_consoleScanner.next());
            }
            catch (NumberFormatException e) {
                System.out.println("\n\tIncorrect number format! Try again:");
            }
        }
    }

    private void openScoreTable() {
        System.out.println("\n\n\n\t*******Score table*******");
        var playerList = _gameModel.getScoreTable().getScoreTable();
        if (playerList.isEmpty()) {
            System.out.println("\t\t Table is empty");
            System.out.println("\t*************************");
            return;
        }
        StringBuilder tableWindow = new StringBuilder();
        int place = 1;
        for (Pair<String, Integer> player : playerList) {
            tableWindow.append("\t");
            tableWindow.append(getPlayerRank(place++));
            tableWindow.append(". ");
            tableWindow.append(player.getKey());
            tableWindow.append(" - ");
            tableWindow.append(player.getValue()).append(" points");
            System.out.println(tableWindow);
            tableWindow.setLength(EMPTY_STRING);
        }
        System.out.println("\t**************************\n");
    }

    private void removePlayerRequest() {
        System.out.println("\n\tWrite player name to remove");
        System.out.print("\t\t       or");
        System.out.println("\n\tPress r to return to the menu.");
        listenRespondAboutScoreTable(false);
    }

    private void addPlayerRequest() {
        System.out.println("\n\tWrite down your name ");
        System.out.print("\t\t     or");
        System.out.println("\n\tPress r to return to the menu.");
        listenRespondAboutScoreTable(true);
    }

    private String getPlayerRank(int place) {
        switch (place) {
            case GODLIKE_RANK -> {
                return GODLIKE;
            }
            case DIVINE_RANK -> {
                return DIVINE;
            }
            case CHAMPION_RANK -> {
                return CHAMPION;
            }
            case KNIGHT_RANK -> {
                return KNIGHT;
            }
            default -> {
                return Loon;
            }
        }
    }
    
    private void listenReturnButton() {
        String button;
        while (true) {
            button = _consoleScanner.next();
            if (button.equals(RETURN)) return;
            else {
                System.out.println("\n\tPress r to return to the menu.");
            }
        }
    }

    private void listenRespondAboutScoreTable(boolean action) {
        String respond;
        while(true) {
            respond = _consoleScanner.next();
            if (respond.equals(RETURN)) break;
            try {
                if (action) {
                    _playerAction.defineAction(SweeperController.PLAYER_NAME,
                            respond, PlayerAction.ActionType.SAVE_SCORE);
                    _playerAction.defineAction(SweeperController.PLAYER_SCORE,
                            _gameModel.getCurScore(), PlayerAction.ActionType.SAVE_SCORE);
                } else {
                    _playerAction.defineAction(SweeperController.PLAYER,
                            respond, PlayerAction.ActionType.REMOVE_SCORE);
                }
                _gameController.processPlayerAction(_playerAction);
                openScoreTable();
                removePlayerRequest();
            } catch (NumberFormatException | GameException e) {
                System.err.println(e.getMessage());
                System.out.println("\n\tPress r to return to the menu.");
            }
        }
        invokeGameMenuWindow();
    }

    private void printGameInfo() {
        System.out.print("\n\n\n\t******Minesweeper about******");
        System.out.println("*************************");
        System.out.println(_gameModel.getAboutInfo());
        System.out.print("\t*****************************");
        System.out.println("*************************");
        System.out.println("\n\tPress r to return to the menu.");
        listenReturnButton();
    }

    private void exitGame() {
        try {
            serializeScoreTable();
            pseudoClearConsole();
            System.exit(SweeperController.EXIT_FAILURE);
        } catch (GameException e) {
            System.out.println(e.getMessage());
        }
    }

    private void serializeScoreTable() throws GameException {
        _playerAction.defineAction(IGNORE_PARAMETERS, IGNORE_PARAMETERS,
                PlayerAction.ActionType.SERIALIZE_SCORE);
        _gameController.processPlayerAction(_playerAction);
    }

    private void pseudoClearConsole() {
        for(int clear = 0; clear < 500; clear++) {
            System.out.println("\b") ;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent gameEvent) {
        try {
            switch (gameEvent.getPropertyName()) {
                case MineSweeper.IGNORE_EVENT -> {
                }
                case MineSweeper.REQUEST_ACTION_EVENT -> requestForPlayerAction();
                case MineSweeper.FIELD_CHANGE_EVENT -> {
                    printGameField();
                    printTimeAndScore();
                    requestForPlayerAction();
                }
                case MineSweeper.CHANGE_GAME_STAGE_EVENT -> {
                    if (_gameField == null) {
                        _gameField = _gameModel.getGameField();
                    }
                    respondToNewStage((MineSweeper.GameStage) gameEvent.getNewValue());
                }
            }
        } catch (GameException e) {
            System.err.println(e.getMessage());
        }
    }

    private void requestForPlayerAction() {
        try {
            showAvailableActionList();
            while(_playerAction.isInvalidAction()) {
                defineAction(_consoleScanner.nextLine().split(DELIMITER_ACTION_PARAMETERS));
            }
            _gameController.processPlayerAction(_playerAction);
        } catch (GameException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showAvailableActionList() {
        if (_gameModel.isVictoryStage()) {
            System.out.println("\t***Congratulations!**********");
            System.out.println("\ts. Save score.");
            System.out.println("\te. End game and exit to menu.");
            System.out.println("\t*****************************");
        } else {
            System.out.print("\t********Action List****************");
            System.out.println("********************");
            System.out.println("\tw. Write coords to open cell \"[coordinate] [coordinate]\".");
            System.out.println("\tf. Setup flag to mark mine \"[coordinate] [coordinate]\".");
            System.out.println("\te. End game and exit to menu.");
            System.out.print("\t****************************");
            System.out.println("***************************");
        }
    }

    private void defineAction(String[] action) {
        switch(action[FIRST_PARAMETER]) {
            case EXIT -> {
                if (action.length == UNARY_ACTION) {
                    _playerAction.defineAction(SweeperController.EXIT_FROM_GAME,
                            EXIT, PlayerAction.ActionType.EXIT);
                } else {
                    System.out.println("\tAction cant be executed! Please, try again.");
                    _playerAction.defineAction(SweeperController.INVALID_ACTION,
                            IGNORE_PARAMETERS, PlayerAction.ActionType.INVALID);
                }
            }
            case CLICK -> {
                try {
                    if (action.length == TERNARY_ACTION) {
                        _playerAction.defineAction(SweeperController.COORDINATE_X,
                                Integer.parseInt(action[SECOND_PARAMETER]), PlayerAction.ActionType.CLICK);
                        _playerAction.defineAction(SweeperController.COORDINATE_Y,
                                Integer.parseInt(action[THIRD_PARAMETER]), PlayerAction.ActionType.CLICK);
                    } else {
                        throw new ActionParameterException();
                    }
                } catch (NumberFormatException | ActionParameterException ex) {
                    System.out.println("\tAction cant be executed! Please, try again.");
                    _playerAction.defineAction(SweeperController.INVALID_ACTION,
                            IGNORE_PARAMETERS, PlayerAction.ActionType.INVALID);
                }
            }
            case FLAG -> {
                try {
                    if (action.length != TERNARY_ACTION) {
                        throw new ActionParameterException();
                    }
                    _playerAction.defineAction(SweeperController.COORDINATE_X,
                            Integer.parseInt(action[SECOND_PARAMETER]), PlayerAction.ActionType.FLAG);
                    _playerAction.defineAction(SweeperController.COORDINATE_Y,
                            Integer.parseInt(action[THIRD_PARAMETER]), PlayerAction.ActionType.FLAG);
                } catch (NumberFormatException | ActionParameterException ex) {
                    System.out.println("\tAction cant be executed! Please, try again.");
                    _playerAction.defineAction(SweeperController.INVALID_ACTION,
                            IGNORE_PARAMETERS, PlayerAction.ActionType.INVALID);
                }
            }
            case SCORE -> {
                try {
                    openScoreTable();
                    addPlayerRequest();
                } catch (NumberFormatException ex) {
                    System.out.println("\tAction cant be executed! Please, try again.");
                    _playerAction.defineAction(SweeperController.INVALID_ACTION,
                         IGNORE_PARAMETERS, PlayerAction.ActionType.INVALID);
                }
            }
            default -> {
                _playerAction.defineAction(SweeperController.INVALID_ACTION,
                        IGNORE_PARAMETERS, PlayerAction.ActionType.INVALID);
            }
        }
    }

    private void printGameField() {
        StringBuilder gameWindow = new StringBuilder();
        gameWindow.append("   ");
        for (var i = 0; i < _gameField.getFieldCol(); ++i) {
            gameWindow.append(printWindowBoard(i));
        }
        System.out.println(gameWindow);

        for (var i = 0; i < _gameField.getFieldRow(); ++i) {
            gameWindow.setLength(EMPTY_STRING);
            gameWindow.append(RESET_COLOR);
            gameWindow.append(printWindowBoard(i));
            for (var j = 0; j < _gameField.getFieldCol(); ++j) {
                gameWindow.append(printSpaces(j));
                printCell(_gameField.locateCell(j, i), gameWindow);
            }
            System.out.println(gameWindow);
        }
    }

    private String printWindowBoard(int num) {
        String str = Integer.toString(num);
        if (str.length() == TWO_DIGIT_NUMBER) {
            str += "  ";
        } else {
            str += " ";
        }
        return str;
    }

    private String printSpaces(int j) {
        if (Integer.toString(j).length() == TWO_DIGIT_NUMBER) {
            return "  ";
        } else {
            return " ";
        }
    }

    private void printCell(Cell cell, StringBuilder gameWindow) {
        if (cell.isOpened()) {
            if (cell.isMine()) {
                gameWindow.append(MINE_IMAGE);
            } else if (cell.isFlag()) {
                gameWindow.append(FLAG_IMAGE);
            } else {
                gameWindow.append(defineColorForCell(cell));
                gameWindow.append(cell.getMinesAround());
                gameWindow.append(RESET_COLOR);
            }
        } else {
            if (cell.isFlag()) {
                gameWindow.append(RESET_COLOR);
                gameWindow.append(FLAG_IMAGE);
            } else {
                gameWindow.append(CLOSED_CELL_IMAGE);
            }
        }
    }

    private String defineColorForCell(Cell cell) {
        if (cell.isMine()) return RED_COLOR;
        switch (cell.getMinesAround()) {
            case 0 -> {
                return WHITE_COLOR;
            }
            case 1 -> {
                return GREEN_COLOR;
            }
            case 2 -> {
                return CYAN_COLOR;
            }
            case 3 -> {
                return BLUE_COLOR;
            }
            case 4 -> {
                return YELLOW_COLOR;
            }
            case 5 -> {
                return MAGENTA_COLOR;
            }
            default -> {
                return RED_COLOR;
            }
        }
    }

    private void printTimeAndScore() {
        System.out.println("\t| Time: " + _gameModel.getCurGameTime() + " sec "
                + "\t| Score: " + _gameModel.getCurScore() + " points "
                + "\t| Available flags: " + _gameModel.getAvailableFlags() + " |\n");
    }

    private void respondToNewStage(MineSweeper.GameStage newStage) throws GameException {
        switch (newStage) {
            case ACTION -> {
                printGameField();
                printTimeAndScore();
                requestForPlayerAction();
            }
            case DEFEAT -> {
                System.out.println("\n\n\n\t********DEFEAT!********");
                printGameField();
                System.out.println("\n\tPress r to return to the menu.");
                listenReturnButton();
                invokeGameMenuWindow();
            }
            case VICTORY -> {
                System.out.println("\n\n\n\n\t*******VICTORY!*******");
                printGameField();
                requestForPlayerAction();
                System.out.println("\n\tPress r to return to the menu.");
                listenReturnButton();
                invokeGameMenuWindow();
            }
            case CLOSED -> {
                invokeGameMenuWindow();
            }
        }
    }
}
