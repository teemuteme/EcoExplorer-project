package fi.tuni.application.classes;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javafx.scene.image.Image;

public class HourlyViewItem {
    private long epochTime;
    private Image weatherImage;
    private String temp;
    private Image windImage;
    private String windSpeed;
    private String rainChance;

    public HourlyViewItem(long epochTime, Image weatherImage, String temp, Image windImage, String windSpeed,
            String rainChance) {
        this.epochTime = epochTime;
        this.weatherImage = weatherImage;
        this.temp = temp;
        this.windImage = windImage;
        this.windSpeed = windSpeed;
        this.rainChance = rainChance;
    }

    public String getTimeLabel() {
        Instant instant = Instant.ofEpochSecond(this.epochTime);

        // Create a formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm")
                .withZone(ZoneId.systemDefault());

        // Format the Instant to a readable date-time string
        return formatter.format(instant);
    }

    public long getEpochTime() {
        return epochTime;
    }

    public Image getWeatherImage() {
        return weatherImage;
    }

    public void setWeatherImage(Image weatherImage) {
        this.weatherImage = weatherImage;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public Image getWindImage() {
        return windImage;
    }

    public void setWindImage(Image windImage) {
        this.windImage = windImage;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getRainChance() {
        return rainChance;
    }

    public void setRainChance(String rainChance) {
        this.rainChance = rainChance;
    }

}