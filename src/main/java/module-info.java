

module com.example.minesweeper {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    exports game.display.gui to javafx.graphics;
    exports game.exception to javafx.graphics;


    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
}