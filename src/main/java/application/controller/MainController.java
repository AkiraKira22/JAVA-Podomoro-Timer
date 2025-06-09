package application.controller;

import application.MusicPlayer;
import application.model.TimerModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;

import java.io.IOException;
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
    @FXML private javafx.scene.canvas.Canvas progressCanvas;
    @FXML private Slider volumeSlider;
    @FXML private HBox sessionTrackerBar;
    @FXML private StackPane muteStack;

    private boolean isRunning = false;
    private final TimerModel timerModel;
    private boolean isFocusState = true;
    private int focusDuration = 25;
    private int restDuration = 5;
    private int totalTimeInSeconds;
    public static Stage settingsStage = null;
    private String currentPlaylist = "";
    private int completedSessions = 0;

    public MainController() {
        timerModel = new TimerModel();
    }

    public void setPrimaryStage(Stage stage) {
        stage.setOnCloseRequest(e -> {
            if (settingsStage != null) {
                settingsStage.close();
            }
            javafx.application.Platform.exit();
        });
    }

    @FXML
    public void initialize() {
        updateTimerDisplay();

        MusicPlayer musicPlayer = MusicPlayer.getInstance();

        if (musicPlayer != null && muteUnmuteButton != null) {
            updateMuteUnmuteIcon(musicPlayer.isMuted());
        }

        totalTimeInSeconds = focusDuration * 60;

        // Timer listeners
        timerModel.minutesProperty().addListener((obs, oldVal, newVal) -> {
            updateTimerDisplay();
            drawProgressCircle();
        });
        timerModel.secondsProperty().addListener((obs, oldVal, newVal) -> {
            updateTimerDisplay();
            drawProgressCircle();
        });
        timerModel.setOnTimerFinished(this::onTimerFinished);

        // Initialize volume slider with current volume (0-100)
        volumeSlider.setValue(musicPlayer.getVolume());

        // Volume slider change listener
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double newVolume = newVal.doubleValue();
            musicPlayer.setVolume(newVolume);
            updateMuteUnmuteIcon(musicPlayer.isMuted());
        });

        muteStack.setOnMouseEntered(e -> volumeSlider.setVisible(true));
        muteStack.setOnMouseExited(e -> volumeSlider.setVisible(false));

        drawProgressCircle();
    }

    private void onTimerFinished() {
        playBellSound();
        if (isFocusState) {
            isFocusState = false;
            updateStateDisplay();
            totalTimeInSeconds = restDuration * 60;
            timerModel.setTimer(restDuration, 0);

            addCoffeeCupIcon();
        } else {
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
        }
        else {
            timerModel.startTimer();
            playPauseIcon.setImage(new Image(Objects.requireNonNull(getClass().getResource("/pause_black.png")).toString()));
            if (musicPlayer != null) {
                if (musicPlayer.playlist.isEmpty()) {
                    musicPlayer.setPresetPlaylist("cafe");
                }
                musicPlayer.resumeSong();
            }
        }
        isRunning = !isRunning;
    }

    public void changePlaylist(String playlistName) {
        MusicPlayer musicPlayer = MusicPlayer.getInstance();

        if (!playlistName.equalsIgnoreCase(currentPlaylist)) {
            currentPlaylist = playlistName;
            musicPlayer.setPresetPlaylist(currentPlaylist);

            if (isRunning) {
                musicPlayer.playCurrentSong();
            }
        }
    }

    @FXML
    private void handleSettings() {
        if (settingsStage == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Settings.fxml"));
                Parent root = loader.load();

                application.controller.SettingsController settingsController = loader.getController();
                settingsController.setTimerPresetListener((focus, rest) -> {
                    applyPreset(focus, rest);
                });
                settingsController.setPlaylistChangeListener(playlistName -> {
                    changePlaylist(playlistName);
                });

                settingsStage = new Stage();
                settingsStage.setTitle("Settings");
                settingsStage.setScene(new Scene(root));
                settingsStage.setResizable(false);
                settingsStage.show();

                settingsStage.setOnCloseRequest(e -> settingsStage = null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            settingsStage.setIconified(false);
            settingsStage.toFront();
            settingsStage.requestFocus();
        }
    }

    @FXML
    private void handleMuteUnmute() {
        MusicPlayer musicPlayer = MusicPlayer.getInstance();
        if (musicPlayer != null) {
            musicPlayer.toggleMute();
            javafx.application.Platform.runLater(() -> {
                updateMuteUnmuteIcon(musicPlayer.isMuted());
            });
            if (musicPlayer.isMuted()) {
                volumeSlider.setValue(0);
            } else {
                volumeSlider.setValue(musicPlayer.getVolume());
            }
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

    @FXML
    private void handleTimerClicked(javafx.scene.input.MouseEvent event) {
        if (event.getClickCount() == 2) {
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

    private void addCoffeeCupIcon() {
        javafx.application.Platform.runLater(() -> {
            ImageView cup;
             if (focusDuration >= 50) {
            // 50 min7yte: big cup
                cup = new ImageView(new Image(getClass().getResource("/coffee_big.png").toString()));
                cup.setFitWidth(24);
                cup.setFitHeight(24);
            } else {
                // 25 minute: smol cup
                cup = new ImageView(new Image(getClass().getResource("/coffee_small.png").toString()));
                cup.setFitWidth(24);
                cup.setFitHeight(24);
            }
            sessionTrackerBar.getChildren().add(cup);
        });
    }
    @FXML
    private void handleEndSession() {
        timerModel.pauseTimer();
        onTimerFinished(); 
    }
}
