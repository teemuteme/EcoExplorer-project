package fi.tuni.application.Utilities;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.node.ArrayNode;
import fi.tuni.application.classes.WeatherQualityData;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class WeatherQualityDataRetriever {
    ObjectMapper objectMapper = new ObjectMapper();
    public String airVisualApiKey = "API_KEY";

    public WeatherQualityData fetchData(double lat, double lon) {
        String endpoint = MessageFormat.format(
                "http://api.airvisual.com/v2/nearest_city?lat={0}&lon={1}&key={2}",
                lat, lon, airVisualApiKey);

        try {
            return fetchData(endpoint);
        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("An error occurred while fetching air quality data data. Please try again later.");
            alert.setContentText(e.getMessage());

            // Show the alert
            alert.showAndWait();

        }
        return null;
    }

    private WeatherQualityData fetchData(String endpoint) throws IOException {
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
                JsonNode node = objectMapper.readValue(responseAsString, JsonNode.class);
                JsonNode data = node.get("data");
                JsonNode current = data.get("current");
                JsonNode pollution = current.get("pollution");
                JsonNode weather = current.get("weather");

                // Idea was to add forecast data from qualities, but we noticed that free API version doesn't support forecasts
                //List<JsonNode> forecasts = new ArrayList<JsonNode>(data.??("forecasts"));


                ObjectMapper mapper = new ObjectMapper();
                //ArrayList<JsonNode> list = (ArrayList<JsonNode>) Collections.singletonList(forecasts);

                int humidity = weather.get("hu").asInt();
                int airQuality = pollution.get("aqius").asInt();
                List<WeatherQualityData.QualityForecastInfo> forecastList = new ArrayList<WeatherQualityData.QualityForecastInfo>();

                /*for (int i = 0; i < forecastList.size(); i++) {
                    WeatherQualityData.QualityForecastInfo forecast = new WeatherQualityData.QualityForecastInfo(list[i].getTs(), 234);
                }*/

                return new WeatherQualityData(humidity, airQuality, forecastList);
            } else {
                throw new IOException("Response body is null");
            }
        }

    }
}
