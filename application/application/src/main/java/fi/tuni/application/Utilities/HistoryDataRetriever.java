package fi.tuni.application.Utilities;

import java.io.IOException;
import java.text.MessageFormat;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.tuni.application.classes.DailyHistoryData;
import fi.tuni.application.classes.HourlyHistoryData;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HistoryDataRetriever {
    ObjectMapper objectMapper = new ObjectMapper();
    public String openWeatherMapApiKey = "API_KEY";
    // public String airVisualApiKey = "API_KEY";

    public DailyHistoryData fetchDailyData(double lat, double lon) {

        String endpoint = MessageFormat.format(
                "https://history.openweathermap.org/data/2.5/history/city?lat={0}&lon={1}&cnt=7&type=hour&units=metric&appid={2}",
                Double.toString(lat).replace(',', '.'), Double.toString(lon).replace(',', '.'), openWeatherMapApiKey);

        try {
            return fetchDailyData(endpoint);
        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("An error occurred while fetching history data. Please try again later.");
            alert.setContentText(e.getMessage());

            // Show the alert
            alert.showAndWait();
        }
        return null;
    }

    public HourlyHistoryData fetchHourlyData(double lat, double lon) {

        String endpoint = MessageFormat.format(
                // Doesn't work like we want, commented part which calls this function
                "https://history.openweathermap.org/data/2.5/history/city?lat={0}&lon={1}&cnt=24&type=day&units=metric&appid={2}",
                Double.toString(lat).replace(',', '.'), Double.toString(lon).replace(',', '.'), openWeatherMapApiKey);

        try {
            return fetchHourlyData(endpoint);
        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("An error occurred while fetching history data. Please try again later.");
            alert.setContentText(e.getMessage());

            // Show the alert
            alert.showAndWait();
        }
        return null;
    }

    private DailyHistoryData fetchDailyData(String endpoint) throws IOException {
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
                return this.objectMapper.readValue(responseAsString, DailyHistoryData.class);
            } else {
                throw new IOException("Response body is null");
            }
        }
    }

    private HourlyHistoryData fetchHourlyData(String endpoint) throws IOException {
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
                return this.objectMapper.readValue(responseAsString, HourlyHistoryData.class);
            } else {
                throw new IOException("Response body is null");
            }
        }
    }

}
