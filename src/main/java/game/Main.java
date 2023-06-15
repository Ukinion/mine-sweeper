package game;

import game.display.gui.GUI;
import game.exception.GameException;

public class Main {
    public static void main(String[] argc) {
        try {
            GUI.invokeGameMenu();
        } catch (GameException e) {
            System.out.println(e.getMessage());
        }
    }
}
