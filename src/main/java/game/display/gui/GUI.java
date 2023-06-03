package game.display.gui;

import game.exception.GameException;
import game.exception.GameLaunchException;
import game.logic.SweeperController;
import game.model.MineSweeper;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class GUI extends Application implements PropertyChangeListener {
    private static final int MIN_WIDTH = 100;

    private Stage _mainStage;
    private VBox _menu;

    private static MineSweeper _gameModel;
    private static SweeperController _gameController;

    @Override
    public void start(Stage primaryStage) {
        _mainStage = primaryStage;
        _mainStage.setTitle("Minesweeper");
        _gameModel.addListener(this);
        setupMainStage();
    }

    private void setupMainStage() {
        createMenuLayout();
        createFieldLayout();
        createMainLayout();
        Scene scene = new Scene();
        _mainStage.setScene(scene);
        _mainStage.setWidth(600);
        _mainStage.setHeight(600);
        _mainStage.show();
    }

    private void createMenuLayout() {
        _menu = new VBox();
        _menu.getChildren().add(createStartButton());
        _menu.getChildren().add(createRecordsButton());
        _menu.getChildren().add(createAboutButton());
        _menu.getChildren().add(createExitButton());
        _menu.getChildren().add(createTimerAndScoreVBox());
        _menu.setMinWidth(MIN_WIDTH);
    }

    private Button createStartButton() {
        Button startButton = new Button("Start");
        startButton.setOnMouseClicked(mouseEvent -> {

        });
        return startButton;
    }

    @Override
    public void propertyChange(PropertyChangeEvent gameEvent) {

    }

    public static void startGUI(MineSweeper gameModel, SweeperController controller) throws GameException {
        if (gameModel == null || controller == null) {
            throw new GameLaunchException("Game start failed: " +
                    "required objects are at bad state");
        }
        _gameModel = gameModel;
        _gameController = controller;
        System.out.println("Thread startGUI: " + Thread.currentThread());
        launch();
    }

}
