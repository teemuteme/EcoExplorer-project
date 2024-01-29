module fi.tuni.application {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.desktop;

    requires org.controlsfx.controls;
    requires java.net.http;
    requires org.json;
    requires okhttp3;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;

    opens fi.tuni.application to javafx.fxml;

    exports fi.tuni.application;
    exports fi.tuni.application.classes;
    exports fi.tuni.application.Utilities to com.fasterxml.jackson.databind;
}