package nsu.ccfit.fomin.display.gui;

import nsu.ccfit.fomin.exception.GameException;
import nsu.ccfit.fomin.exception.ImageLoadingException;
import nsu.ccfit.fomin.logic.PlayerAction;
import nsu.ccfit.fomin.logic.SweeperController;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import nsu.ccfit.fomin.objects.unit.Cell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

import java.io.FileInputStream;
import java.io.IOException;

import static nsu.ccfit.fomin.constants.GameConstants.EMPTY_STRING;

public class CellButton extends Button {
    private static final String flagImage_ = "F";
    private static final String mineImage_ = "B";

    private final Cell _cell;
    private PlayerAction _action;
    private SweeperController _controller;

//    private static final String FLAG_IMAGE_URL = "src/main/resources/flag.png";
//    private static final String MINE_IMAGE_URL = "src/main/resources/mine.png";
//    public static void initButtonImages() throws GameException {
//        try (FileInputStream inputImageFile = new FileInputStream(FLAG_IMAGE_URL)) {
//            flagImage_ = new Image(inputImageFile, 30, 30, false, false);
//        } catch (IOException e) {
//            throw new ImageLoadingException("Flag");
//        }
//        try (FileInputStream inputImageFile = new FileInputStream(MINE_IMAGE_URL)) {
//            mineImage_ = new Image(inputImageFile, 30, 30, false, false);
//        } catch (IOException e) {
//            throw new ImageLoadingException("Mine");
//        }
//    }

    public CellButton(Cell cell) {
        _cell = cell;
        _action = new PlayerAction();
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
        this.setGraphic(null);
        if (_cell.isOpened()) {
            if (_cell.isMine()) {
                this.setText(mineImage_);
            } else {
                this.setText(Integer.valueOf(_cell.getMinesAround()).toString());
            }
        } else if (_cell.isFlag()) {
            this.setText(flagImage_);
        } else {
            this.setText(EMPTY_STRING);
        }
    }

    public void setController(SweeperController controller) {
        _controller = controller;
    }
}
