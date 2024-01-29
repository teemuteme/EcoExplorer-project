package fi.tuni.application.classes;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javafx.scene.image.Image;

public class DailyViewItem {
    private Image image;
    private long epochTime;
    private String tempLabel;
    private String description;

    public DailyViewItem(Image image, long epochTime, String tempLabel, String description) {
        this.image = image;
        this.epochTime = epochTime;
        this.tempLabel = tempLabel;
        this.description = description;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getDateLabel() {
        Instant instant = Instant.ofEpochSecond(this.epochTime);

        // Create a formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd")
                .withZone(ZoneId.systemDefault());

        // Format the Instant to a readable date-time string
        return formatter.format(instant);
    }

    public long getEpochTime() {
        return epochTime;
    }

    public String getTempLabel() {
        return tempLabel;
    }

    public void setTempLabel(String label2) {
        this.tempLabel = label2;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}