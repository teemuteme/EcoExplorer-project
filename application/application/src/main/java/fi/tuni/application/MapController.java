package fi.tuni.application;

import java.util.List;

import fi.tuni.application.Utilities.CityToCoordinateMapper;
import fi.tuni.application.Utilities.WeatherDataRetriever;
import fi.tuni.application.Utilities.WeatherQualityDataRetriever;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Pair;

public class MapController extends BaseSceneController {

    @FXML
    private StackPane mapPane;
    @FXML
    private ImageView mapImage;

    @FXML
    private GridPane cityGrid;

    @FXML
    protected void initialize() {
        super.initialize();

        // Add clickable dots on cities
        addCityDot(mapPane, new City(-10, 230, "Helsinki"));
        addCityDot(mapPane, new City(0, 22, "Oulu"));
        addCityDot(mapPane, new City(-63, 225, "Turku"));
        addCityDot(mapPane, new City(-35, 175, "Tampere"));
        addCityDot(mapPane, new City(-75, 178, "Pori"));
        addCityDot(mapPane, new City(7, -50, "Rovaniemi"));
        addCityDot(mapPane, new City(-75, 110, "Vaasa"));
        addCityDot(mapPane, new City(-55, 125, "Seinäjoki"));
        addCityDot(mapPane, new City(-48, 77, "Kokkola"));
        addCityDot(mapPane, new City(5, 145, "Jyväskylä"));
        addCityDot(mapPane, new City(0, 196, "Lahti"));
        addCityDot(mapPane, new City(30, 220, "Kotka"));
        addCityDot(mapPane, new City(52, 198, "Lappeenranta"));
        addCityDot(mapPane, new City(90, 125, "Joensuu"));
        addCityDot(mapPane, new City(40, 52, "Kajaani"));
        addCityDot(mapPane, new City(40, 110, "Kuopio"));
        addCityDot(mapPane, new City(35, 165, "Mikkeli"));
        addCityDot(mapPane, new City(-22, 200, "Hämeenlinna"));

        List<Pair<String, String>> data = List.of(
                new Pair<>("City", ""),
                new Pair<>("Temperature", ""),
                new Pair<>("Temperature max", ""),
                new Pair<>("Temperature min", ""),
                new Pair<>("Wind", ""),
                new Pair<>("Clouds", ""),
                new Pair<>("Description", ""),
                new Pair<>("Air Quality", ""),
                new Pair<>("humidity", ""));

        // Add the data to the grid
        for (int i = 0; i < data.size(); i++) {
            Pair<String, String> pair = data.get(i);

            Label label1 = new Label(pair.getKey());
            cityGrid.add(label1, 0, i);

            Label label2 = new Label(pair.getValue());
            cityGrid.add(label2, 1, i);
        }
    }

    private void addCityDot(StackPane mapPane, City city) {
        Circle dot = new Circle(11, Color.RED); // Adjust the radius and color as needed
        dot.setTranslateX(city.getX());
        dot.setTranslateY(city.getY());

        // Attach city name as a user data to the dot
        dot.setUserData(city.getCityName());
        dot.setOnMouseClicked(event -> handleDotClick(dot));
        mapPane.getChildren().add(dot);
    }

    private void handleDotClick(Circle dot) {
        this.cityGrid.getChildren().clear();
        String cityName = (String) dot.getUserData();

        var weatherData = new WeatherDataRetriever().fetchData(cityName, "metric");
        var city = CityToCoordinateMapper.getCity(cityName);
        var weatherQualityData = new WeatherQualityDataRetriever().fetchData(city.getLatitude(),
                city.getLongitude());

        // Set up the columns
        List<Pair<String, String>> data = List.of(
                new Pair<>("City", weatherData.getName()),
                new Pair<>("Temperature", Double.toString(weatherData.getMain().getTemp()) + " C"),
                new Pair<>("Temperature max", Double.toString(weatherData.getMain().getTemp_max()) + " C"),
                new Pair<>("Temperature min", Double.toString(weatherData.getMain().getTemp_min()) + " C"),
                new Pair<>("Wind", Double.toString(weatherData.getWind().getSpeed()) + "m/s"),
                new Pair<>("Clouds", Integer.toString(weatherData.getClouds().getAll()) + "%"),
                new Pair<>("Description", weatherData.getWeather()[0].getDescription()),
                new Pair<>("Air Quality", Integer.toString(weatherQualityData.getAirQuality())),
                new Pair<>("humidity", Integer.toString(weatherQualityData.getHumidity()))

        );

        // Add the data to the grid
        for (int i = 0; i < data.size(); i++) {
            Pair<String, String> pair = data.get(i);

            Label label1 = new Label(pair.getKey());
            cityGrid.add(label1, 0, i);

            Label label2 = new Label(pair.getValue());
            cityGrid.add(label2, 1, i);
        }

    }

    private static class City {
        private double x;
        private double y;
        private String cityName;

        public City(double x, double y, String cityName) {
            this.x = x;
            this.y = y;
            this.cityName = cityName;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public String getCityName() {
            return cityName;
        }
    }
}
