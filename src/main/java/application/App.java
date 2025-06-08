package application;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        MusicPlayerGUIFX musicPlayerGUIX = new MusicPlayerGUIFX();
        musicPlayerGUIX.showPlayerWindow();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
