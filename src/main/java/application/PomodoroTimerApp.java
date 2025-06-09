package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PomodoroTimerApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/main.fxml"));
        Parent root = loader.load();

        // Pass the primaryStage to the controller
        application.controller.MainController mainController = loader.getController();
        mainController.setPrimaryStage(primaryStage);

        primaryStage.setTitle("Pomodoro Timer");
        primaryStage.setScene(new Scene(root, 300, 400));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}