package application.controller;

import application.model.TimerModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;

public class MainController {

    @FXML private Label timerLabel;
    @FXML private Label stateLabel;
    @FXML private Button playPauseButton;
    @FXML private VBox mainLayout;
    @FXML private Button settingsButton;

    private boolean isRunning = false;
    private TimerModel timerModel;
    private boolean isFocusState = true;

    public MainController() {
        timerModel = new TimerModel();
    }

    @FXML
    public void initialize() {
        updateTimerDisplay();

        timerModel.minutesProperty().addListener((obs, oldVal, newVal) -> updateTimerDisplay());
        timerModel.secondsProperty().addListener((obs, oldVal, newVal) -> updateTimerDisplay());

        // Set timer finished callback
        timerModel.setOnTimerFinished(this::onTimerFinished);
    }

    private void onTimerFinished() {
        playBellSound();
        if (isFocusState) { // Transition to Rest
            isFocusState = false;
            updateStateDisplay();
            timerModel.setTimer(1, 2); // 5 minutes rest
            timerModel.startTimer();
        } else { // Transition to Focus
            isFocusState = true;
            updateStateDisplay();
            timerModel.setTimer(25, 0); // 25 minutes focus
            timerModel.startTimer();
        }
    }

    private void playBellSound() {
        String sound = getClass().getResource("/application/bell.wav").toString();
        AudioClip bell = new AudioClip(sound);
        bell.play();
    }

    private void updateStateDisplay() {
        javafx.application.Platform.runLater(() -> {
            stateLabel.setText(isFocusState ? "Focus" : "Rest");
        });
    }

    private void updateTimerDisplay() {
        javafx.application.Platform.runLater(() -> {
            timerLabel.setText(timerModel.getFormattedTime());
        });
    }

    @FXML
    private void handlePlayPause() {
        if (isRunning) {
            timerModel.pauseTimer();
            playPauseButton.setText("Play");
        } else {
            timerModel.startTimer();
            playPauseButton.setText("Pause");
        }
        isRunning = !isRunning;
    }

    @FXML
    private void handleSettings() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/application/settings.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Settings");
            stage.setScene(new javafx.scene.Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    

    // Additional methods to update the timer display can be added here
}