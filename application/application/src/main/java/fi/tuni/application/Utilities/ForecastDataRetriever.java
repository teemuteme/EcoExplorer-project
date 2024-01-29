package fi.tuni.application.Utilities;

import java.io.IOException;
import java.text.MessageFormat;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.tuni.application.classes.DailyForecastData;
import fi.tuni.application.classes.HourlyForecastData;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ForecastDataRetriever {
    ObjectMapper objectMapper = new ObjectMapper();
    public String openWeatherMapApiKey = "API_KEY";
    public String airVisualApiKey = "API_KEY";

    public DailyForecastData fetchDailyData(double lat, double lon) {

        String endpoint = MessageFormat.format(
                "https://api.openweathermap.org/data/2.5/forecast/daily?lat={0}&lon={1}&cnt=7&units=metric&appid={2}",
                lat, lon, openWeatherMapApiKey);

        try {
            return fetchDailyData(endpoint);
        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("An error occurred while fetching daily data.");
            alert.setContentText(e.getMessage());

            // Show the alert
            alert.showAndWait();

        }
        return null;
    }

    public HourlyForecastData fetchHourlyData(double lat, double lon) {

        String endpoint = MessageFormat.format(
                "https://pro.openweathermap.org/data/2.5/forecast/hourly?lat={0}&lon={1}&units=metric&appid={2}", lat,
                lon, openWeatherMapApiKey);

        try {
            return fetchHourlyData(endpoint);
        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("An error occurred while fetching hourly data. Please try again later.");
            alert.setContentText(e.getMessage());

            // Show the alert
            alert.showAndWait();

        }
        return null;
    }

    private DailyForecastData fetchDailyData(String endpoint) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(endpoint)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }

            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String responseAsString = responseBody.string();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                return this.objectMapper.readValue(responseAsString, DailyForecastData.class);
            } else {
                throw new IOException("Response body is null");
            }
        }
    }

    private HourlyForecastData fetchHourlyData(String endpoint) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(endpoint)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }

            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String responseAsString = responseBody.string();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                return this.objectMapper.readValue(responseAsString, HourlyForecastData.class);
            } else {
                throw new IOException("Response body is null");
            }
        }
    }

}
