package fi.tuni.application;

import java.text.MessageFormat;

import fi.tuni.application.classes.DailyForecastData;
import fi.tuni.application.classes.DailyViewItem;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class DailyViewController {
    private HourlyViewController hourlyViewController;

    @FXML
    private ListView<DailyViewItem> dailyView;
    private DailyForecastData dailyForecastData;

    protected void setHourlyController(HourlyViewController hourlyViewController) {
        this.hourlyViewController = hourlyViewController;
    }

    public void initialize() {
        dailyView.setCellFactory(param -> new ListCell<DailyViewItem>() {
            private ImageView imageView = new ImageView();
            private Label label1 = new Label();
            private Label label2 = new Label();
            private Label description = new Label();

            @Override
            protected void updateItem(DailyViewItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(item.getImage());
                    imageView.setFitWidth(100);
                    imageView.setFitHeight(100);
                    label1.setText(item.getDateLabel());
                    label2.setText(item.getTempLabel() + "°C");
                    description.setText(item.getDescription());

                    VBox vbox = new VBox(imageView, label1, label2, description);
                    setGraphic(vbox);
                }
            }

        });
        dailyView.setOnMouseClicked(event -> {
            DailyViewItem selectedItem = dailyView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                hourlyViewController.populateHourlyWeatherData(selectedItem.getEpochTime());
            }
        });
    }

    protected void populate() {
        for (int i = 0; i < dailyForecastData.getList().size(); i++) {
            var dailyForecastItem = dailyForecastData.getList().get(i);
            Image image = new Image(MessageFormat.format(
                    "https://openweathermap.org/img/wn/{0}@4x.png",
                    dailyForecastItem.getWeather().get(0).getIcon()));
            String tempLabel = Double.toString(dailyForecastItem.getTemp().getDay());
            String description = dailyForecastItem.getWeather().get(0).getDescription();

            DailyViewItem item = new DailyViewItem(image, dailyForecastItem.getDt(), tempLabel, description);

            dailyView.getItems().add(item);
        }
        dailyView.getSelectionModel().select(0);
    }

    protected void setData(DailyForecastData dailyForecastData) {
        this.dailyForecastData = dailyForecastData;
    }
}