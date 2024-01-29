package fi.tuni.application;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;

import fi.tuni.application.Utilities.WeatherIconLoader;
import fi.tuni.application.classes.HourlyForecastData;
import fi.tuni.application.classes.HourlyViewItem;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class HourlyViewController {
    @FXML
    private ListView<HourlyViewItem> hourlyView;
    private HourlyForecastData hourlyForecastData;

    public void initialize() {
        hourlyView.setCellFactory(param -> new ListCell<HourlyViewItem>() {
            private ImageView weatherImage = new ImageView();
            private Label timeLabel = new Label();
            private Label tempLabel = new Label();
            private Label windSpeedLabel = new Label();
            private Label rainChanceLabel = new Label();
            private ImageView windImage = new ImageView();

            @Override
            protected void updateItem(HourlyViewItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    timeLabel.setText(item.getTimeLabel());
                    tempLabel.setText(item.getTemp());
                    weatherImage.setImage(item.getWeatherImage());
                    weatherImage.setFitWidth(100);
                    weatherImage.setFitHeight(100);
                    windImage.setImage(item.getWindImage());
                    windSpeedLabel.setText(item.getWindSpeed());
                    rainChanceLabel.setText(item.getRainChance());

                    VBox vbox = new VBox(timeLabel, weatherImage, tempLabel, windImage, windSpeedLabel,
                            rainChanceLabel);
                    vbox.setAlignment(Pos.CENTER);
                    vbox.setMaxHeight(Double.MAX_VALUE); // make the VBox take up as much height as possible
                    vbox.setFillWidth(true); // make the VBox take up the full width of the ListView
                    setGraphic(vbox);
                }
            }
        });

    }

    protected void setData(HourlyForecastData hourlyForecastData) {
        this.hourlyForecastData = hourlyForecastData;
    }

    protected void populateHourlyWeatherData(long date) {
        hourlyView.getItems().clear();

        // Convert the given date to an Instant
        Instant givenDate = Instant.ofEpochSecond(date);

        for (int i = 0; i < hourlyForecastData.getList().size(); i++) {
            var hourlyForecastItem = hourlyForecastData.getList().get(i);

            // Convert the item's datetime to an Instant
            Instant itemDate = Instant.ofEpochSecond(hourlyForecastItem.getDt());

            // Check if the item's datetime is within 24 hours of the given date
            if (Math.abs(Duration.between(givenDate, itemDate).toHours()) <= 12) {
                Image image = new Image(MessageFormat.format(
                        "https://openweathermap.org/img/wn/{0}@4x.png",
                        hourlyForecastItem.getWeather().get(0).getIcon()));
                Image windImage = WeatherIconLoader.createArrowImage(hourlyForecastItem.getWind().getDeg());
                String temp = hourlyForecastItem.getMain().getTemp() + "°C";
                String rainChance = "Rain chance: " + hourlyForecastItem.getPop() + "%";
                String windSpeed = "Wind speed: " + hourlyForecastItem.getWind().getSpeed() + " m/s";

                HourlyViewItem item = new HourlyViewItem(hourlyForecastItem.getDt(), image, temp, windImage, windSpeed,
                        rainChance);
                hourlyView.getItems().add(item);
            }
        }
    }
}