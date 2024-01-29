package fi.tuni.application.Utilities;

import java.io.IOException;
import java.text.MessageFormat;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.tuni.application.classes.WeatherData;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class WeatherDataRetriever {
    ObjectMapper objectMapper = new ObjectMapper();
    public String openWeatherMapApiKey = "API_KEY";

    public WeatherData fetchData(String cityName, String units) {
        String endpoint = MessageFormat.format(
                "https://api.openweathermap.org/data/2.5/weather?q={0}&units={1}&appid={2}",
                cityName, units, openWeatherMapApiKey);

        try {
            return fetchData(endpoint);
        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("An error occurred while fetching weather data.");
            alert.setContentText(e.getMessage());

            // Show the alert
            alert.showAndWait();

        }
        return null;
    }

    private WeatherData fetchData(String endpoint) throws IOException {
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
                return this.objectMapper.readValue(responseAsString, WeatherData.class);
            } else {
                throw new IOException("Response body is null");
            }
        }
    }
}
