package fi.tuni.application;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class SidebarController {
    private boolean visible = false;

    @FXML
    protected void OnExitButtonPressed() {
        javafx.application.Platform.exit();
        System.exit(0);
    }

    @FXML
    private VBox sidebar;

    @FXML
    private Button mapBtn;
    @FXML
    private Button graphicsBtn;
    @FXML
    private Button frontpageBtn;
    @FXML
    private Button StatisticsBtn;

    @FXML
    protected void SwitchVisible() {
        visible = !visible;
        sidebar.setVisible(visible);
        sidebar.setManaged(visible);
    }

    private void changeScene(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
            Parent root = loader.load();
            Scene scene = mapBtn.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void openFrontpageScene() {
        changeScene("welcome.fxml");
    }

    @FXML
    protected void openGraphicsScene() {
        changeScene("graphics.fxml");

    }

    @FXML
    protected void openStatisticsScene() {
        changeScene("statics.fxml");
    }

    @FXML
    protected void openMapScene() {
        changeScene("map.fxml");
    }
}
