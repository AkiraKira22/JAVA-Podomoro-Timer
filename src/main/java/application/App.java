package application;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        MusicPlayerGUI musicPlayerGUI = new MusicPlayerGUI(primaryStage);
        musicPlayerGUI.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
