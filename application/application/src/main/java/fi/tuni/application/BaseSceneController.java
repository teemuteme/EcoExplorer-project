package fi.tuni.application;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

import fi.tuni.application.Utilities.CityToCoordinateMapper;
import fi.tuni.application.Utilities.DataStorage;
import fi.tuni.application.Utilities.ForecastDataRetriever;
import fi.tuni.application.Utilities.HistoryDataRetriever;
import fi.tuni.application.Utilities.WeatherDataRetriever;
import fi.tuni.application.Utilities.WeatherQualityDataRetriever;
import fi.tuni.application.classes.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BaseSceneController {
    protected String currentCity;

    private PropertyChangeSupport support;
    protected WeatherData weatherData;
    protected DailyForecastData dailyForecastData;
    protected DailyHistoryData dailyHistoryData;
    protected HourlyForecastData hourlyForecastData;
    protected HourlyHistoryData hourlyHistoryData;
    protected WeatherQualityData weatherQualityData;

    public BaseSceneController() {
        support = new PropertyChangeSupport(this);
    }

    public void setCurrentCity(String currentCity) {
        String oldCity = this.currentCity;
        this.currentCity = currentCity;
        support.firePropertyChange("currentCity", oldCity, currentCity);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    @FXML
    private BorderPane root;

    @FXML
    protected void initialize() {
        FXMLLoader sidebarLoader = new FXMLLoader(getClass().getResource("sidebar.fxml"));
        FXMLLoader topbarLoader = new FXMLLoader(getClass().getResource("topbar.fxml"));

        VBox sidebar;
        HBox topbar;
        try {
            sidebar = sidebarLoader.load();
            topbar = topbarLoader.load();
        } catch (IOException e) {
            e.printStackTrace();

            // Create an alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(
                    "An error occurred while loading the FXML files. Please try again or contect the developers.");
            alert.setContentText(e.getMessage());

            // Show the alert
            alert.showAndWait();

            return; // Exit the method
        }
        sidebar.setVisible(false);
        sidebar.setManaged(false);

        SidebarController sidebarController = sidebarLoader.getController();
        TopbarController topbarController = topbarLoader.getController();

        topbarController.setSidebarController(sidebarController);

        root.setLeft(sidebar);
        root.setTop(topbar);

        Button selectHomeBtn = (Button) topbar.lookup("#selectHomeCityBtn");
        selectHomeBtn.setOnAction(e -> selectHomeCity());
        Button selectCurrentBtn = (Button) topbar.lookup("#selectCurrentCityBtn");
        selectCurrentBtn.setOnAction(e -> selectCurrentCity());

        this.currentCity = DataStorage.readUserData().getCurrentCity();
        this.addPropertyChangeListener(evt -> {

            fetchData();

        });
        fetchData();
    }

    private void fetchData() {
        this.weatherData = new WeatherDataRetriever().fetchData(currentCity, "metric");
        var forecastDataRetriever = new ForecastDataRetriever();
        var historyDataRetriever = new HistoryDataRetriever();
        var city = CityToCoordinateMapper.getCity(currentCity);
        this.hourlyForecastData = forecastDataRetriever.fetchHourlyData(city.getLatitude(),
                city.getLongitude());
        this.hourlyHistoryData = historyDataRetriever.fetchHourlyData(city.getLatitude(),
                city.getLongitude());
        this.dailyForecastData = forecastDataRetriever.fetchDailyData(city.getLatitude(),
                city.getLongitude());
        // API doesn't support daily historic. Would need to make own API call for each day. Time ran out
        //this.dailyHistoryData = historyDataRetriever.fetchDailyData(city.getLatitude(),
        //        city.getLongitude());
        this.weatherQualityData = new WeatherQualityDataRetriever().fetchData(city.getLatitude(),
                city.getLongitude());
    }

    public void selectHomeCity() {
        // Create a TextField and a ListView
        TextField textField = new TextField();
        ListView<String> listView = new ListView<>();

        // Create a FilteredList
        ObservableList<String> cityNames = FXCollections.observableArrayList(CityToCoordinateMapper.getAllCityNames());
        FilteredList<String> filteredCityNames = new FilteredList<>(cityNames, p -> true);

        listView.setItems(filteredCityNames);
        // Add a listener to the TextField text property
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredCityNames.setPredicate(cityName -> cityName.toLowerCase().contains(newVal.toLowerCase()));
            listView.setItems(filteredCityNames);
            listView.getSelectionModel().selectFirst(); // Select the first item in the list
        });

        textField.setText(DataStorage.readUserData().getHomeCity());

        // Create a ButtonType for submitting the answer
        ButtonType submitButtonType = new ButtonType("Submit", ButtonData.OK_DONE);
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().getButtonTypes().add(submitButtonType);

        Button submitButton = (Button) dialog.getDialogPane().lookupButton(submitButtonType);

        // Add an event filter to the submit button
        submitButton.addEventFilter(ActionEvent.ACTION, event -> {
            // If nothing is selected, show an error message and consume the event
            String selectedCity = listView.getSelectionModel().getSelectedItem();
            if (selectedCity == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please select a city.");
                alert.showAndWait();
                event.consume(); // Consume the event to prevent the dialog from closing
            }
        });

        // Set the result converter
        dialog.setResultConverter(buttonType -> {
            if (buttonType == submitButtonType) {
                // Save the selected city as the home city
                String selectedCity = listView.getSelectionModel().getSelectedItem();
                if (selectedCity != null) {
                    try {
                        DataStorage.saveUserData(new UserOptions(selectedCity, this.currentCity));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                return selectedCity;
            }
            return null;
        });

        // Show the TextField, ListView, and Button in a dialog
        VBox content = new VBox(textField, listView);
        dialog.setTitle("Select Home City");
        dialog.setHeaderText("Enter your home city.");
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    @FXML
    public void selectCurrentCity() {
        // Create a TextField and a ListView
        TextField textField = new TextField();
        ListView<String> listView = new ListView<>();

        // Create a FilteredList
        ObservableList<String> cityNames = FXCollections.observableArrayList(CityToCoordinateMapper.getAllCityNames());
        FilteredList<String> filteredCityNames = new FilteredList<>(cityNames, p -> true);

        listView.setItems(filteredCityNames);
        // Add a listener to the TextField text property
        textField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredCityNames.setPredicate(cityName -> cityName.toLowerCase().contains(newVal.toLowerCase()));
            listView.setItems(filteredCityNames);
            listView.getSelectionModel().selectFirst(); // Select the first item in the list
        });

        textField.setText(DataStorage.readUserData().getCurrentCity());

        // Create a ButtonType for submitting the answer
        ButtonType submitButtonType = new ButtonType("Submit", ButtonData.OK_DONE);
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().getButtonTypes().add(submitButtonType);

        Button submitButton = (Button) dialog.getDialogPane().lookupButton(submitButtonType);

        // Add an event filter to the submit button
        submitButton.addEventFilter(ActionEvent.ACTION, event -> {
            // If nothing is selected, show an error message and consume the event
            String selectedCity = listView.getSelectionModel().getSelectedItem();
            if (selectedCity == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Please select a city.");
                alert.showAndWait();
                event.consume(); // Consume the event to prevent the dialog from closing
            }
        });

        // Set the result converter
        dialog.setResultConverter(buttonType -> {
            if (buttonType == submitButtonType) {
                // Save the selected city as the current city
                String selectedCity = listView.getSelectionModel().getSelectedItem();
                if (selectedCity != null) {
                    setCurrentCity(selectedCity); // Use the setter method to change the current city
                    try {
                        DataStorage.saveUserData(
                                new UserOptions(DataStorage.readUserData().getHomeCity(), this.currentCity));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return selectedCity;
            }
            return null;
        });

        // Show the TextField, ListView, and Button in a dialog
        VBox content = new VBox(textField, listView);
        dialog.setTitle("Select Current City");
        dialog.setHeaderText("Enter your current city.");
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

}
