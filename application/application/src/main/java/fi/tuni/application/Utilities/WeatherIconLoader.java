package fi.tuni.application.Utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class WeatherIconLoader {
    public static Image getWeatherIcon(String iconCode) {
        Image icon = null;
        try {
            icon = new Image(
                    new FileInputStream(
                            "src/main/resources/fi/tuni/application/icons/" + iconCode + ".png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return icon;
    }

    public static Image createArrowImage(double angle) {
        Rectangle shaft = new Rectangle(0, 3, 40, 6);
        shaft.setFill(Color.BLACK);

        // Create a triangle for the head of the arrow
        Polygon arrowHead = new Polygon(40, 0, 55, 6, 40, 12);
        arrowHead.setFill(Color.BLACK);

        // Group the shaft and the head
        Group arrow = new Group(shaft, arrowHead);

        // Rotate the arrow
        arrow.setRotate(angle);

        // Create an image from the arrow shape
        WritableImage wImage = new WritableImage(60, 60);

        // Create snapshot parameters with a transparent fill
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);

        // Take the snapshot with the transparent parameters
        arrow.snapshot(parameters, wImage);

        return wImage;
    }
}
