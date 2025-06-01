package application;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {
        PomodoroGUIX pomodoroGUIX = new PomodoroGUIX();
        pomodoroGUIX.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
