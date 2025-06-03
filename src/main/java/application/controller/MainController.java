package application.controller;

import application.MusicPlayer;
import application.model.TimerModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

public class MainController {

    @FXML private Label timerLabel;
    @FXML private Label stateLabel;
    @FXML private Button playPauseButton;
    @FXML private VBox mainLayout;
    @FXML private Button settingsButton;

    private boolean isRunning = false;
    private final TimerModel timerModel;
    private boolean isFocusState = true;
    private int focusDuration = 25;
    private int restDuration = 5;
    private int totalTimeInSeconds;
    public static javafx.stage.Stage settingsStage = null; // Add this line
    public static javafx.stage.Stage musicPlayerStage = null; // Change from private to public

    public void setPrimaryStage(Stage stage) {
        // Listen for main window close and close all secondary windows
        stage.setOnCloseRequest(e -> {
            if (settingsStage != null) {
                settingsStage.close();
            }
            if (musicPlayerStage != null) {
                musicPlayerStage.close();
            }
            javafx.application.Platform.exit();
        });
    }

    public MainController() {
        timerModel = new TimerModel();
    }

    @FXML
    public void initialize() {
        updateTimerDisplay();

        totalTimeInSeconds = focusDuration * 60;

        timerModel.minutesProperty().addListener((obs, oldVal, newVal) -> {
            updateTimerDisplay();
            drawProgressCircle();
        });
        timerModel.secondsProperty().addListener((obs, oldVal, newVal) -> {
            updateTimerDisplay();
            drawProgressCircle();
        });
        // Set timer finished callback
        timerModel.setOnTimerFinished(this::onTimerFinished);

        drawProgressCircle();
    }

    private void onTimerFinished() {
        playBellSound();
        if (isFocusState) { // Transition to Rest
            isFocusState = false;
            updateStateDisplay();
            totalTimeInSeconds = restDuration * 60;
            timerModel.setTimer(restDuration, 0);
        } else { // Transition to Focus
            isFocusState = true;
            updateStateDisplay();
            totalTimeInSeconds = focusDuration * 60;
            timerModel.setTimer(focusDuration, 0);
        }
        timerModel.startTimer();
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
        if (settingsStage != null && settingsStage.isShowing()) {
            settingsStage.toFront(); // Optionally bring it to front
            return;
        }
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/application/settings.fxml"));
            javafx.scene.Parent root = loader.load();

            SettingsController settingsController = loader.getController();
            settingsController.setTimerPresetListener((focus, rest) -> {
                // Stop timer first
                timerModel.pauseTimer();
                isRunning = false;
                playPauseButton.setText("Play");

                this.focusDuration = focus;
                this.restDuration = rest;
                totalTimeInSeconds = isFocusState ? focus * 60 : rest * 60;
                timerModel.setTimer(isFocusState ? focusDuration : restDuration, 0);
                updateTimerDisplay();
            });

            settingsStage = new javafx.stage.Stage();
            settingsStage.setTitle("Settings");
            settingsStage.setScene(new javafx.scene.Scene(root));
            settingsStage.setResizable(false);
            settingsStage.setOnCloseRequest(e -> settingsStage = null); // Reset when closed
            settingsStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   
    @FXML
    private void handleTimerClicked(javafx.scene.input.MouseEvent event) {
        if (event.getClickCount() == 2) { // Double-click detect
            if (isFocusState) {
                totalTimeInSeconds = focusDuration * 60;
                timerModel.setTimer(focusDuration, 0);
            } else {
                totalTimeInSeconds = restDuration * 60;
                timerModel.setTimer(restDuration, 0);
            }
            updateTimerDisplay();
        }
    }


    @FXML
    private javafx.scene.canvas.Canvas progressCanvas;

    private void drawProgressCircle() {
        javafx.application.Platform.runLater(() -> {
            if (totalTimeInSeconds == 0) return;

            double currentTime = timerModel.minutesProperty().get() * 60 + timerModel.secondsProperty().get();
            double elapsedTime = totalTimeInSeconds - currentTime;
            double progress = elapsedTime / totalTimeInSeconds;


            var gc = progressCanvas.getGraphicsContext2D();
            gc.clearRect(0, 0, progressCanvas.getWidth(), progressCanvas.getHeight());

            double centerX = progressCanvas.getWidth() / 2;
            double centerY = progressCanvas.getHeight() / 2;
            double radius = 100;
            double strokeWidth = 20;

            // Draw base circle
            gc.setStroke(Color.LIGHTGRAY);
            gc.setLineWidth(strokeWidth);
            gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

            // Draw progress circle
            double angle = progress * 360;
            gc.setStroke(Color.WHITESMOKE);
            gc.setLineWidth(strokeWidth);
            gc.strokeArc(centerX - radius, centerY - radius, radius * 2, radius * 2,
                    90, -angle, ArcType.OPEN);
        });
    }
}