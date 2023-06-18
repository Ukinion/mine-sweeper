

module com.example.minesweeper {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    exports nsu.ccfit.fomin.display.gui to javafx.graphics;
    exports nsu.ccfit.fomin.exception to javafx.graphics;
    exports nsu.ccfit.fomin.objects.unit to javafx.graphics;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.desktop;
}