package game.display.gui;

import game.exception.GameException;
import game.logic.PlayerAction;
import game.logic.SweeperController;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import game.objects.unit.Cell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

import java.io.FileInputStream;
import java.io.IOException;

public class CellButton extends Button {
    private static final String FLAG_IMAGE_URL = "flag.png";
    private static final String MINE_IMAGE_URL = "mine.png";

    private static final int LOADING_FAILED = -0x5;

    private static Image flagImage_;
    private static Image mineImage_;

    private final Cell _cell;
    private PlayerAction _action;
    private SweeperController _controller;

    static {
        try (FileInputStream inputImageFile = new FileInputStream(FLAG_IMAGE_URL)) {
            flagImage_ = new Image(inputImageFile, 25, 25, false, false);
        } catch (IOException e) {
            System.err.println("Flag image loading failed!");
            System.err.println(e.getMessage());
            System.exit(LOADING_FAILED);
        }
        try (FileInputStream inputImageFile = new FileInputStream(MINE_IMAGE_URL)) {
            mineImage_ = new Image(inputImageFile, 25, 25, false, false);
        } catch (IOException e) {
            System.err.println("Mine image loading failed!");
            System.err.println(e.getMessage());
            System.exit(LOADING_FAILED);
        }
    }

    public CellButton(Cell cell) {
        _cell = cell;
        this.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                _action.defineAction(SweeperController.COORDINATE_X, _cell.getCoordinateX(),
                        PlayerAction.ActionType.FLAG);
                _action.defineAction(SweeperController.COORDINATE_Y, _cell.getCoordinateY(),
                        PlayerAction.ActionType.FLAG);
            } else {
                _action.defineAction(SweeperController.COORDINATE_X, _cell.getCoordinateX(),
                        PlayerAction.ActionType.CLICK);
                _action.defineAction(SweeperController.COORDINATE_Y, _cell.getCoordinateY(),
                        PlayerAction.ActionType.CLICK);
            }
            try {
                _controller.processPlayerAction(_action);
            } catch (GameException e) {
                System.err.println(e.getMessage());
            }
        });
    }

    public void updateButtonImage() {
        if (_cell.isOpened()) {
            if (_cell.isMine()) {
                this.setGraphic(new ImageView(mineImage_));
            } else {
                this.setGraphic(null);
                this.setText(Integer.valueOf(_cell.getMinesAround()).toString());
            }
        } else if (_cell.isFlag()) {
            this.setGraphic(new ImageView(flagImage_));
        } else {
            this.setGraphic(null);
        }
    }

    public void setController(SweeperController controller) {
        _controller = controller;
    }
}
