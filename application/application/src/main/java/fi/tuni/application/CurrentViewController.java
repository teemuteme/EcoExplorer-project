package fi.tuni.application;

import java.text.MessageFormat;

import fi.tuni.application.classes.WeatherData;
import fi.tuni.application.classes.WeatherQualityData;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CurrentViewController {
    @FXML
    private ImageView currentWeatherImage;

    @FXML
    private VBox currentDataVbox;

    protected void populateCurrentWeatherData(WeatherData weatherData, WeatherQualityData weatherQualityData) {
        HBox hbox1 = new HBox();
        hbox1.setAlignment(Pos.CENTER);
        Label label1 = new Label("Location: " + weatherData.getName());
        hbox1.getChildren().add(label1);

        HBox hbox2 = new HBox();
        hbox2.setPrefHeight(100.0);
        hbox2.setPrefWidth(200.0);
        hbox2.setAlignment(Pos.CENTER);
        ImageView currentWeatherImage = new ImageView();
        Image randomWeatherIcon = new Image(MessageFormat.format(
                "https://openweathermap.org/img/wn/{0}@4x.png",
                weatherData.getWeather()[0].getIcon()));

        currentWeatherImage.setImage(randomWeatherIcon);
        currentWeatherImage.setFitHeight(150.0);
        currentWeatherImage.setFitWidth(200.0);
        currentWeatherImage.setPickOnBounds(true);
        currentWeatherImage.setPreserveRatio(true);
        Label label2 = new Label("Temperature: " + weatherData.getMain().getTemp() + "°C");
        hbox2.getChildren().addAll(currentWeatherImage, label2);

        HBox hbox3 = new HBox();
        hbox3.setAlignment(Pos.CENTER);
        Label feelsLike = new Label("Feels like: " + weatherData.getMain().getFeels_like() + "°C");
        hbox3.getChildren().add(feelsLike);

        HBox hbox4 = new HBox();
        hbox4.setPrefHeight(100.0);
        hbox4.setPrefWidth(200.0);
        hbox4.setAlignment(Pos.CENTER);
        hbox4.setSpacing(20.0);
        Label label3 = new Label("Air Quality: " + weatherQualityData.getAirQuality() + "AQI");
        Label label6 = new Label("Humidity: " + weatherQualityData.getHumidity() + "%");
        Label windSpeed = new Label("Wind Speed: " + weatherData.getWind().getSpeed() + " m/s");
        Label cloudiness = new Label("Cloudiness: " + weatherData.getClouds().getAll() + "%");
        hbox4.getChildren().addAll(label3, label6, windSpeed, cloudiness);
        currentDataVbox.getChildren().addAll(hbox1, hbox2, hbox3, hbox4);
    }

}
