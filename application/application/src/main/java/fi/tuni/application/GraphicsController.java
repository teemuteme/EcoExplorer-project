package fi.tuni.application;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GraphicsController extends BaseSceneController {
    @FXML
    private BorderPane bp;
    @FXML
    private BorderPane root;

    private CurrentViewController currentViewController;
    private DailyViewController dailyViewController;
    private HourlyViewController hourlyViewController;

    @FXML
    protected void initialize() {
        super.initialize();
        super.addPropertyChangeListener(evt -> {
            if ("currentCity".equals(evt.getPropertyName())) {
                this.updateWeatherData();

            }
        });
        updateWeatherData();

    }

    private void updateWeatherData() {
        // Load currentView.fxml
        FXMLLoader currentLoader = new FXMLLoader(getClass().getResource("currentView.fxml"));
        FXMLLoader dailyLoader = new FXMLLoader(getClass().getResource("dailyView.fxml"));
        FXMLLoader hourlyLoader = new FXMLLoader(getClass().getResource("hourlyView.fxml"));
        AnchorPane currentView;
        AnchorPane dailyView;
        AnchorPane hourlyView;
        try {
            currentView = currentLoader.load();
            dailyView = dailyLoader.load();
            hourlyView = hourlyLoader.load();
        } catch (IOException e) {
            e.printStackTrace();

            // Create an alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(
                    "An error occurred while loading the FXML files. Please try again later or contact the developer");
            alert.setContentText(e.getMessage());

            // Show the alert
            alert.showAndWait();

            return; // Exit the method
        }

        currentViewController = currentLoader.getController();
        currentViewController.populateCurrentWeatherData(weatherData, weatherQualityData);

        // Load dailyView.fxml
        dailyViewController = dailyLoader.getController();
        dailyViewController.setData(dailyForecastData);
        dailyViewController.populate();

        // Load hourlyView.fxml
        hourlyViewController = hourlyLoader.getController();
        hourlyViewController.setData(hourlyForecastData);

        hourlyViewController.populateHourlyWeatherData(dailyForecastData.getList().get(0).getDt());

        dailyViewController.setHourlyController(hourlyViewController);

        // Add all views to a VBox and set it as the center of the root BorderPane
        VBox vbox = new VBox(currentView, dailyView, hourlyView);
        bp.setCenter(vbox);
    }
}