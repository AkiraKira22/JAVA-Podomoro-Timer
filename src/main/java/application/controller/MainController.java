package application.controller;

import application.MusicPlayer;
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
    private int focusDuration = 25;
    private int restDuration = 5;

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
            timerModel.setTimer(restDuration, 0); 
            timerModel.startTimer();
        } else { // Transition to Focus
            isFocusState = true;
            updateStateDisplay();
            timerModel.setTimer(focusDuration, 0); 
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
        MusicPlayer musicPlayer = MusicPlayer.getInstance();
        if (isRunning) {
            timerModel.pauseTimer();
            playPauseButton.setText("Play");
            if (musicPlayer != null) {
                musicPlayer.pauseSong();
            }
        } else {
            timerModel.startTimer();
            playPauseButton.setText("Pause");
            if (musicPlayer != null) {
                musicPlayer.resumeSong();
            }
        }
        isRunning = !isRunning;
    }

    @FXML
    private void handleSettings() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/application/settings.fxml"));
            javafx.scene.Parent root = loader.load();

            SettingsController settingsController = loader.getController();
            settingsController.setTimerPresetListener((focus, rest) -> {
                this.focusDuration = focus;
                this.restDuration = rest;
                timerModel.setTimer(focusDuration, 0);
                updateTimerDisplay();
            });

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Settings");
            stage.setScene(new javafx.scene.Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    @FXML
    private void handleTimerClicked(javafx.scene.input.MouseEvent event) {
        if (event.getClickCount() == 2) { // Double-click detect
            if (isFocusState) {
                timerModel.setTimer(focusDuration, 0); 
            } else {
                timerModel.setTimer(restDuration, 0); 
            }
            updateTimerDisplay();
        }
    }


}