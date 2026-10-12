package ie.setu.campsite;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// main class - loads home.fxml and opens the window
public class CampsiteApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(CampsiteApp.class.getResource("home.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        stage.setTitle("Campsite Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
