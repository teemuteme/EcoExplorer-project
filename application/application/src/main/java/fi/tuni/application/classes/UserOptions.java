package fi.tuni.application.classes;

public class UserOptions {
    private String homeCity;
    private String currentCity;

    public UserOptions(String homeCity, String currentCity) {
        this.homeCity = homeCity;
        this.currentCity = currentCity;
    }

    public String getHomeCity() {
        return homeCity;
    }

    public String getCurrentCity() {
        return currentCity;
    }

}
