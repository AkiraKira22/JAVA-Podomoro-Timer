package application.controller;

import application.MusicPlayer;
import application.MusicPlayerGUIFX;
import application.model.TimerModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

import java.util.Objects;

public class MainController {

    @FXML private Label timerLabel;
    @FXML private Label stateLabel;
    @FXML private Button playPauseButton;
    @FXML private ImageView playPauseIcon;
    @FXML private ImageView muteUnmuteIcon;
    @FXML private VBox mainLayout;
    @FXML private Button settingsButton;
    @FXML private Button muteUnmuteButton;

    private MusicPlayerGUIFX musicPlayerWindow;

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

        MusicPlayer musicPlayer = MusicPlayer.getInstance();
        if (musicPlayer != null && muteUnmuteButton != null) {
            updateMuteUnmuteIcon(musicPlayer.isMuted());
        }

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
        String sound = Objects.requireNonNull(getClass().getResource("/application/bell.wav")).toString();
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
            playPauseIcon.setImage(new Image(Objects.requireNonNull(getClass().getResource("/play_black.png")).toString()));
            if (musicPlayer != null) {
                musicPlayer.pauseSong();
            }
        } else {
            timerModel.startTimer();
            playPauseIcon.setImage(new Image(Objects.requireNonNull(getClass().getResource("/pause_black.png")).toString()));
            if (musicPlayer != null) {
                musicPlayer.resumeSong();
            }
        }
        isRunning = !isRunning;
    }

    @FXML
    private void handleSettings() {
        ContextMenu settingsMenu = new ContextMenu();

        // Preset Submenu
        Menu presetMenu = new Menu("Preset");
        MenuItem preset1_1 = new MenuItem("1 + 1");
        MenuItem preset25_5 = new MenuItem("25 + 5");
        MenuItem preset50_10 = new MenuItem("50 + 10");

        preset1_1.setOnAction(e -> applyPreset(1, 1));
        preset25_5.setOnAction(e -> applyPreset(25, 5));
        preset50_10.setOnAction(e -> applyPreset(50, 10));

        presetMenu.getItems().addAll(preset1_1, preset25_5, preset50_10);

        // Music Player
        MenuItem openMusicPlayer = new MenuItem("Music Player");
        openMusicPlayer.setOnAction(e -> openMusicPlayerWindow());

        // DND Toggle
        CheckMenuItem dndToggle = new CheckMenuItem("Do Not Disturb");
        dndToggle.setOnAction(e -> handleDND(dndToggle.isSelected()));

        settingsMenu.getItems().addAll(presetMenu, openMusicPlayer, dndToggle);

        settingsMenu.show(settingsButton, javafx.geometry.Side.BOTTOM, 0, 10);
    }

    @FXML
    private void handleMuteUnmute() {
        MusicPlayer musicPlayer = MusicPlayer.getInstance();
        if (musicPlayer != null) {
            musicPlayer.toggleMute();
            updateMuteUnmuteIcon(musicPlayer.isMuted());
        }
    }

    private void updateMuteUnmuteIcon(boolean isMuted) {
        String iconPath = isMuted ? "/mute_black.png" : "/unmute_black.png";
        muteUnmuteIcon.setImage(new Image(Objects.requireNonNull(getClass().getResource(iconPath)).toString()));
    }

    private void applyPreset(int focus, int rest) {
        timerModel.pauseTimer();
        isRunning = false;
        playPauseIcon.setImage(new Image(Objects.requireNonNull(getClass().getResource("/play_black.png")).toString()));

        this.focusDuration = focus;
        this.restDuration = rest;
        totalTimeInSeconds = isFocusState ? focus * 60 : rest * 60;
        timerModel.setTimer(isFocusState ? focusDuration : restDuration, 0);
        updateTimerDisplay();
    }

    private void openMusicPlayerWindow() {
        if (musicPlayerWindow != null && musicPlayerWindow.getStage() != null && musicPlayerWindow.getStage().isShowing()) {
            musicPlayerWindow.getStage().toFront();
            musicPlayerWindow.getStage().requestFocus();
            return;
        }

        try {
            if (musicPlayerWindow == null) {
                musicPlayerWindow = new MusicPlayerGUIFX();
            }
            musicPlayerWindow.showPlayerWindow();
            Stage stage = musicPlayerWindow.getStage();
            if (stage != null) {
                stage.setOnCloseRequest(e -> {
                    musicPlayerWindow = null;  // clear instance on close
                    musicPlayerStage = null;
                });
                musicPlayerStage = stage;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDND(boolean enabled) {
        // Your logic here
        System.out.println("Do Not Disturb: " + (enabled ? "ON" : "OFF"));
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