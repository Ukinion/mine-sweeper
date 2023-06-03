package game;

import game.display.Console;
import game.display.gui.GUI;
import game.exception.GameException;
import game.logic.SweeperController;
import game.model.MineSweeper;


public class Main {
    public static void main(String[] argc) {
        try {
            GUI.startGUI(new MineSweeper(), new SweeperController());
        } catch (GameException e) {
            System.out.println(e.getMessage());
        }
    }
}
