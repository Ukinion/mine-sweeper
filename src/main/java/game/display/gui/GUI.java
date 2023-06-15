package game.display.gui;

import game.exception.GameException;
import game.logic.PlayerAction;
import game.logic.SweeperController;
import game.model.MineSweeper;
import game.objects.unit.Cell;
import game.objects.unit.ScoreTable;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.util.Pair;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

public class GUI extends Application implements PropertyChangeListener {
    private static final String EMPTY_TABLE = "Table is empty";

    private static final int GODLIKE_RANK = 1;
    private static final int DIVINE_RANK = 2;
    private static final int CHAMPION_RANK = 3;
    private static final int KNIGHT_RANK = 4;

    private static final String GODLIKE = "Godlike";
    private static final String DIVINE = "Divine";
    private static final String CHAMPION = "Champion";
    private static final String KNIGHT = "Knight";
    private static final String Loon  = "Loon";

    private static final String IGNORE_PARAMETERS = "ignore";

    private Stage _mainStage;
    private VBox _menu;
    private GridPane _gameFieldPane;
    private HBox _gameWindowLayout;
    private Label _scoreLabel = new Label("0.0");
    private Label _timerLabel = new Label("0.0");
    private Stage _currentStage;
    private int _fieldCol;

    private ArrayList<CellButton> cellButtons;
    private static MineSweeper _gameModel;
    private static SweeperController _gameController;

    public static void invokeGameMenu() throws GameException {
        _gameController = new SweeperController();
        _gameModel = _gameController.getGameModel();
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        _mainStage = primaryStage;
        _currentStage = primaryStage;
        _mainStage.setTitle("Minesweeper");
        _gameModel.addListener(this);
        setupMainStage();
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    private void setupMainStage() {
        createMenuLayout();
        createFieldLayout();
        createMainLayout();
        Scene scene = new Scene(_gameWindowLayout);
        _mainStage.setScene(scene);
        _mainStage.setMinWidth(200);
        _mainStage.setMinHeight(240);
        _mainStage.setWidth(350);
        _mainStage.setHeight(420);
        _mainStage.show();
    }

    private void createMenuLayout() {
        _menu = new VBox();
        _menu.getChildren().add(createStartButton());
        _menu.getChildren().add(createRecordsButton());
        _menu.getChildren().add(createAboutButton());
        _menu.getChildren().add(createExitButton());
        _menu.getChildren().add(createTimerAndScorePane());
        _menu.setMinWidth(100);
    }

    private Button createStartButton() {
        Button startButton = new Button("Start Game");
        startButton.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        VBox.setVgrow(startButton, Priority.ALWAYS);
        startButton.setOnMouseClicked( mouseEvent -> {
            Stage optionsStage = new Stage();
            _currentStage = optionsStage;
            optionsStage.setTitle("New game options");
            Label fieldColLabel = new Label("Enter field col");
            Label fieldRowLabel = new Label("Enter field row");
            Label minesLabel = new Label("Enter number of mines");
            TextField fieldCol = new TextField();
            TextField fieldRow = new TextField();
            TextField numMines = new TextField();
            Button confirmButton = createConfirmButton(fieldCol, fieldRow, numMines, optionsStage);
            Button returnButton = createReturnButton();
            HBox buttonsBox = new HBox(confirmButton, returnButton);
            buttonsBox.setAlignment(Pos.CENTER);
            VBox optionsBox = new VBox(fieldColLabel, fieldCol, fieldRowLabel, fieldRow, minesLabel, numMines, buttonsBox);
            optionsBox.setAlignment(Pos.CENTER);
            optionsStage.setScene(new Scene(optionsBox));
            optionsStage.initModality(Modality.APPLICATION_MODAL);
            optionsStage.setMinHeight(200);
            optionsStage.setMinWidth(240);
            optionsStage.showAndWait();
        });
        return startButton;
    }

    private Button createConfirmButton(TextField fieldCol, TextField fieldRow, TextField numMines, Stage stage) {
        Button confirmButton = new Button("OK");
        confirmButton.setAlignment(Pos.CENTER);
        confirmButton.setOnAction(action ->{
            try {
                int col = Integer.parseInt(fieldCol.getText());
                _fieldCol = col;
                int row = Integer.parseInt(fieldRow.getText());
                int mines = Integer.parseInt(numMines.getText());
                _gameController.initGameObjects(row, col, mines);
                _gameController.startMineSweeper();
                stage.close();
            } catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });
        return confirmButton;
    }

    private Button createRecordsButton() {
        Button recordsButton = new Button("Score table");
        recordsButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(recordsButton, Priority.ALWAYS);
        recordsButton.setOnMouseClicked(mouseEvent -> {
            Stage recordStage = createRecordStage();
            recordStage.showAndWait();
        });
        return recordsButton;
    }

    private Stage createRecordStage() {
        Stage recordStage = new Stage();
        _currentStage = recordStage;
        recordStage.setTitle("Records");
        recordStage.setScene(new Scene(createRecordVBox()));
        recordStage.initModality(Modality.APPLICATION_MODAL);
        recordStage.setMinHeight(200);
        recordStage.setMinWidth(240);
        return recordStage;
    }

    private VBox createRecordVBox() {
        Label scoreTableLabel = new Label(createRecords(_gameModel.getScoreTable()));
        Label recordsActionLabel = new Label("Enter player name to delete");
        TextField inputField = new TextField();
        Button deleteButton = createRecordDeleteButton(inputField, scoreTableLabel);
        Button returnButton = createReturnButton();
        HBox buttonsBox = new HBox(deleteButton, returnButton);
        buttonsBox.setAlignment(Pos.CENTER);
        VBox recordsMenuBox = new VBox(scoreTableLabel, recordsActionLabel, inputField, buttonsBox);
        recordsMenuBox.setSpacing(5);
        recordsMenuBox.setAlignment(Pos.CENTER);
        return recordsMenuBox;
    }

    private String createRecords(ScoreTable scoreTable) {
        var playerList = scoreTable.getScoreTable();
        if(playerList.size() == 0) {
            return EMPTY_TABLE;
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
        }
        return tableWindow.toString();
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

    private Button createRecordDeleteButton(TextField inputField, Label recordLabel){
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(actionEvent -> {
            try {
                var playerAction = new PlayerAction();
                playerAction.defineAction(SweeperController.PLAYER,
                        inputField.getText(), PlayerAction.ActionType.REMOVE_SCORE);
                _gameController.processPlayerAction(playerAction);
                recordLabel.setText(createRecords(_gameModel.getScoreTable()));
            } catch (Exception e){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });
        deleteButton.setAlignment(Pos.CENTER);
        return deleteButton;
    }

    private Button createReturnButton() {
        Button returnButton = new Button("Back");
        returnButton.setOnMouseClicked(mouseEvent -> {
            _currentStage.close();
        });
        return returnButton;
    }

    private Button createAboutButton() {
        Button aboutButton = new Button("About");
        aboutButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(aboutButton, Priority.ALWAYS);
        aboutButton.setOnMouseClicked( mouseEvent -> {
            Stage aboutStage = new Stage();
            _currentStage = aboutStage;
            aboutStage.setTitle("About");
            Label aboutLabel = new Label(GUI._gameModel.getAboutInfo() + "\n\t\t\t\t\t\t\tNote: right click to set flag.");
            HBox returnButtonBox = new HBox(createReturnButton());
            returnButtonBox.setAlignment(Pos.CENTER);
            VBox aboutMenu = new VBox(aboutLabel, returnButtonBox);
            aboutMenu.setSpacing(5);
            aboutStage.setScene(new Scene(aboutMenu));
            aboutMenu.setAlignment(Pos.CENTER);
            aboutStage.initModality(Modality.APPLICATION_MODAL);
            aboutStage.showAndWait();
        });
        return aboutButton;
    }

    private Button createExitButton() {
        Button exitButton = new Button("Exit");
        exitButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(exitButton, Priority.ALWAYS);
        exitButton.setOnMouseClicked( mouseEvent -> {
            try {
                var playerAction = new PlayerAction();
                playerAction.defineAction(IGNORE_PARAMETERS, IGNORE_PARAMETERS,
                        PlayerAction.ActionType.SERIALIZE_SCORE);
                _gameController.processPlayerAction(playerAction);
            } catch (GameException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText(null);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
            System.exit(0);
        });
        return exitButton;
    }

    private VBox createTimerAndScorePane() {
        VBox pane = new VBox();
        pane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(pane, Priority.ALWAYS);
        Label timerText = new Label("Timer: ");
        Label scoreText = new Label("Score: ");
        _timerLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        _scoreLabel.setMaxSize(50,50);
        HBox timer = new HBox();
        timer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox.setHgrow(_timerLabel, Priority.ALWAYS);
        VBox.setVgrow(timer, Priority.ALWAYS);
        HBox score = new HBox();
        score.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(score, Priority.ALWAYS);
        timer.getChildren().addAll(timerText, _timerLabel);
        score.getChildren().addAll(scoreText, _scoreLabel);
        pane.getChildren().addAll(timer, score);
        return pane;
    }

    private void createFieldLayout() {
        _gameFieldPane = new GridPane();
        _gameFieldPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        HBox.setHgrow(_gameFieldPane, Priority.ALWAYS);
        VBox.setVgrow(_gameFieldPane, Priority.ALWAYS);
        _gameFieldPane.setAlignment(Pos.CENTER);
    }

    private void createMainLayout() {
        _gameWindowLayout = new HBox();
        _gameWindowLayout.getChildren().addAll(_gameFieldPane, _menu);
        _gameWindowLayout.setAlignment(Pos.CENTER_RIGHT);
        _gameWindowLayout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        _gameWindowLayout.setFillHeight(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent gameEvent) {
        String name = gameEvent.getPropertyName();
        switch (name) {
            case MineSweeper.IGNORE_EVENT ->{}
            case MineSweeper.TIMER_TICK -> {
                Platform.runLater( () -> {
                    _timerLabel.setText(Integer.valueOf(_gameModel.getCurGameTime()).toString());
                    _scoreLabel.setText(Double.valueOf(_gameModel.getCurScore()).toString());
                });
            }
            case MineSweeper.FIELD_CHANGE_EVENT -> {
                Cell cell = (Cell) gameEvent.getNewValue();
                updateButton(cell);
                updateField();
            }
            case MineSweeper.CHANGE_GAME_STAGE_EVENT -> {
                respondToNewStage((MineSweeper.GameStage) gameEvent.getNewValue());
            }
        }
    }

    private void updateButton(Cell cell) {
        cellButtons.get(cell.getCoordinateY() * _fieldCol + cell.getCoordinateX()).updateButtonImage();
    }

    private void updateField() {
        for (CellButton cellButton : cellButtons) {
            cellButton.updateButtonImage();
        }
    }

    private void respondToNewStage(MineSweeper.GameStage newStage) {
        switch (newStage) {
            case ACTION -> setupField();
            case DEFEAT ->  {
                updateField();
                showDefeatWindow();
                clearResources();
            }
            case VICTORY -> {
                updateField();
                showVictoryWindow();
                clearResources();
            }
        }
    }

    private void setupField() {
        _gameFieldPane.getChildren().clear();
        cellButtons = new ArrayList<>();
        var gameField = _gameModel.getGameField();
        CellButton.setController(_gameController);
        int fieldCol = gameField.getFieldCol();
        int fieldRow = gameField.getFieldRow();
        Cell cell;
        CellButton cellButton;
        for (int y = 0; y < fieldRow; y++) {
            for (int x = 0; x < fieldCol; ++x) {
                cell = gameField.locateCell(x, y);
                cellButton = new CellButton(cell);
                cellButton.setPrefSize(40, 40);
                cellButton.updateButtonImage();
                _gameFieldPane.add(cellButton, x, y);
                cellButtons.add(y * fieldCol + x, cellButton);
            }
        }
    }

    private void showVictoryWindow() {
        Stage scoreStage = new Stage();
        _currentStage = scoreStage;
        scoreStage.setTitle("Victory");
        Label victoryInfo = new Label("Congratulations!" +
                "\nYour score is " + _gameModel.getCurScore());
        victoryInfo.setAlignment(Pos.CENTER);
        Label saveRequest = new Label("Enter your name ");
        TextField nameField = new TextField();
        Button saveButton = createSaveButton(nameField);
        Button returnButton = createReturnButton();
        HBox buttonsBox = new HBox(saveButton, returnButton);
        buttonsBox.setAlignment(Pos.CENTER);
        VBox mainBox = new VBox(victoryInfo, saveRequest, nameField, buttonsBox);
        mainBox.setAlignment(Pos.CENTER);
        mainBox.setSpacing(5);
        scoreStage.setScene(new Scene(mainBox));
        scoreStage.initModality(Modality.APPLICATION_MODAL);
        scoreStage.setWidth(280);
        scoreStage.setHeight(150);
        scoreStage.showAndWait();
    }

    private Button createSaveButton(TextField nameField) {
        Button saveButton = new Button("Save score");
        saveButton.setOnMouseClicked( mouseEvent -> {
            saveScore(nameField);
            _currentStage.close();
        });
        return saveButton;
    }

    private void showDefeatWindow() {
        Stage defeateStage = new Stage();
        _currentStage = defeateStage;
        defeateStage.setTitle("Defeat");
        Label defeatInfo = new Label("\t\t\t You stepped on a mine!" +
                "\n  Better luck next time...minesweeper, HAHAHHA :)");
        Button returnButton = createReturnButton();
        VBox defeatWindowBox = new VBox(defeatInfo, returnButton);
        defeatWindowBox.setAlignment(Pos.CENTER);
        defeateStage.setScene(new Scene(defeatWindowBox));
        defeateStage.initModality(Modality.APPLICATION_MODAL);
        defeateStage.setWidth(480);
        defeateStage.setHeight(130);
        defeateStage.showAndWait();
    }

    private void saveScore(TextField name) {
        try {
            var playerAction = new PlayerAction();
            playerAction.defineAction(SweeperController.PLAYER_NAME,
                    name.getText(), PlayerAction.ActionType.SAVE_SCORE);
            playerAction.defineAction(SweeperController.PLAYER_SCORE,
                    _gameModel.getCurScore(), PlayerAction.ActionType.SAVE_SCORE);
            _gameController.processPlayerAction(playerAction);
            _currentStage.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void clearResources() {
        _gameFieldPane.getChildren().clear();
        _timerLabel.setText("0.0");
        _scoreLabel.setText("0.0");
    }
}
