package fi.tuni.application.Utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import fi.tuni.application.classes.City;

public class CityToCoordinateMapper {
    private static Map<String, City> cityMap = new HashMap<>();

    static {
        String jsonData = "";
        // Load the JSON data
        try {
            // Load the JSON data
            Scanner scanner = new Scanner(
                    CityToCoordinateMapper.class.getResourceAsStream("/fi/tuni/application/city.list.json"));
            jsonData = scanner.useDelimiter("\\A").next();
            scanner.close();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        // Parse the JSON data
        JSONArray jsonArray = new JSONArray(jsonData);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if ("FI".equals(jsonObject.getString("country"))) {
                // Create a City object
                City city = new City();
                city.setName(jsonObject.getString("name"));
                city.setLatitude(jsonObject.getJSONObject("coord").getDouble("lat"));
                city.setLongitude(jsonObject.getJSONObject("coord").getDouble("lon"));

                // Put the City object in the map
                cityMap.put(city.getName(), city);
            }

        }
    }

    public static City getCity(String name) {
        return cityMap.get(name);
    }

    public static List<String> getAllCityNames() {
        return cityMap.values().stream()
                .map(City::getName)
                .collect(Collectors.toList());
    }
}
