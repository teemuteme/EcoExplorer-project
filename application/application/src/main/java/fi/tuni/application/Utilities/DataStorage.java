package fi.tuni.application.Utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import fi.tuni.application.classes.UserOptions;

public class DataStorage {

    private static final String FILE_PATH = "userdata.txt";

    public static void saveUserData(UserOptions userData) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            String data = userData.getHomeCity() + "\n" + userData.getCurrentCity() + "\n";
            writer.write(data);
        }
    }

    public static UserOptions readUserData() {
        UserOptions userOptions = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String homeCity = reader.readLine();
            String currentCity = reader.readLine();
            if (homeCity != null && currentCity != null) {
                userOptions = new UserOptions(homeCity, currentCity);
            }
        } catch (IOException e) {
            userOptions = new UserOptions("Helsinki", "Tampere");
        }

        return userOptions;
    }

}
