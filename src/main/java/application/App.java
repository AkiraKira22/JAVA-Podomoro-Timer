package application;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        PomodoroGUIFX pomodoroGUIX = new PomodoroGUIFX();
        pomodoroGUIX.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
