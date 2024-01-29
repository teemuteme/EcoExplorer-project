package fi.tuni.application;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

public class TopbarController {

    private SidebarController sidebarController;

    @FXML
    private HBox topbar;

    public void setSidebarController(SidebarController controller) {
        this.sidebarController = controller;
    }

    @FXML
    protected void onMenuIconClick() {
        sidebarController.SwitchVisible();
    }
}
