package com.example.card24;

// Application is the base JavaFX class that starts the desktop window.
import javafx.application.Application;
// FXMLLoader loads the FXML layout file that defines the UI structure.
import javafx.fxml.FXMLLoader;
// Scene represents the visible content inside the window.
import javafx.scene.Scene;
// Stage is the top-level JavaFX window that displays the Scene.
import javafx.stage.Stage;

/**
 * Starts the JavaFX application and loads the main game screen.
 */
public class Card24Application extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Card24Application.class.getResource("card24-view.fxml"));
        Scene scene = new Scene(loader.load(), 760, 480);
        scene.getStylesheets().add(Card24Application.class.getResource("style.css").toExternalForm());
        stage.setTitle("Card 24 Game");
        stage.setScene(scene);
        stage.setMinWidth(760);
        stage.setMinHeight(510);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
