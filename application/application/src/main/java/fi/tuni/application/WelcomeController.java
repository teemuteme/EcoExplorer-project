package fi.tuni.application;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import fi.tuni.application.Utilities.CityToCoordinateMapper;
import fi.tuni.application.Utilities.DataStorage;
import fi.tuni.application.Utilities.WeatherDataRetriever;
import fi.tuni.application.Utilities.WeatherQualityDataRetriever;
import fi.tuni.application.classes.WeatherData;
import fi.tuni.application.classes.WeatherQualityData;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class WelcomeController extends BaseSceneController {

        @FXML
        public Label CityLabel;
        @FXML
        private BorderPane root;

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

                var homeCity = CityToCoordinateMapper.getCity(DataStorage.readUserData().getHomeCity());
                WeatherDataRetriever weatherDataRetriever = new WeatherDataRetriever();
                WeatherData homeweatherData = weatherDataRetriever.fetchData(homeCity.getName(),
                                "metric");

                WeatherQualityDataRetriever weatherQualityDataRetriever = new WeatherQualityDataRetriever();
                WeatherQualityData homeweatherDataweatherQualityData = weatherQualityDataRetriever
                                .fetchData(homeCity.getLatitude(), homeCity.getLongitude());

                VBox homeCityBox = new VBox();
                VBox currentCityBox = new VBox();

                VBox[] vBoxes = { homeCityBox, currentCityBox };
                WeatherData[] weatherDatas = { homeweatherData, weatherData };
                WeatherQualityData[] weatherQualityDatas = { homeweatherDataweatherQualityData, weatherQualityData };

                for (int i = 0; i < vBoxes.length; i++) {
                        addLabelsToBox(vBoxes[i], weatherDatas[i], weatherQualityDatas[i]);
                }

                // Add the boxes to the HBox in the center of the BorderPane
                HBox centerBox = (HBox) root.getCenter();
                centerBox.setSpacing(20); // This will add a space of 20px between the boxes
                centerBox.setPadding(new Insets(15, 12, 15, 12)); // This will add padding around the boxes
                centerBox.getChildren().addAll(homeCityBox, currentCityBox);
        }

        private void addLabelsToBox(VBox box, WeatherData weatherData, WeatherQualityData weatherQualityData) {
                Instant instant1 = Instant.ofEpochSecond(weatherData.getSys().getSunrise());
                Instant instant2 = Instant.ofEpochSecond(weatherData.getSys().getSunset());
                ZonedDateTime zonedDateTime1 = instant1.atZone(ZoneId.systemDefault());
                ZonedDateTime zonedDateTime2 = instant2.atZone(ZoneId.systemDefault());
                String formattedTime1 = zonedDateTime1.format(DateTimeFormatter.ofPattern("HH:mm"));
                String formattedTime2 = zonedDateTime2.format(DateTimeFormatter.ofPattern("HH:mm"));
                box.getChildren().add(new Label("City: " + weatherData.getName()));
                box.getChildren().add(new Label("Temperature: " + weatherData.getMain().getTemp() + "°C"));
                box.getChildren().add(new Label("Air Quality: " + weatherQualityData.getAirQuality() + " AQI"));
                box.getChildren().add(new Label("Humidity: " + weatherQualityData.getHumidity() + "%"));
                box.getChildren().add(new Label("Wind Speed: " + weatherData.getWind().getSpeed() + " m/s"));
                box.getChildren().add(new Label("Wind Direction: " + weatherData.getWind().getDeg() + "°"));
                box.getChildren().add(new Label("Cloudiness: " + weatherData.getClouds().getAll() + "%"));
                box.getChildren().add(new Label("Sunrise: " + formattedTime1));
                box.getChildren().add(new Label("Sunset: " + formattedTime2));
                box.getChildren().add(new Label("Country: " + weatherData.getSys().getCountry()));
                box.getChildren().add(new Label("Visibility: " + weatherData.getVisibility() + " m"));
                box.getChildren()
                                .add(new Label("Weather Description: " + weatherData.getWeather()[0].getDescription()));
                box.getChildren().add(new Label("Feels Like: " + weatherData.getMain().getFeels_like() + "°C"));
                box.getChildren().add(new Label("Min Temperature: " + weatherData.getMain().getTemp_min() + "°C"));
                box.getChildren().add(new Label("Max Temperature: " + weatherData.getMain().getTemp_max() + "°C"));
        }

}