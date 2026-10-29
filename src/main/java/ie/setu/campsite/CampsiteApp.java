package ie.setu.campsite;

import ie.setu.campsite.system.CampsiteManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// main class - loads home.fxml and opens the window
public class CampsiteApp extends Application {

    // one manager for the whole app, every controller uses this
    public static CampsiteManager manager = new CampsiteManager();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(CampsiteApp.class.getResource("home.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 700);
        stage.setTitle("Campsite Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
