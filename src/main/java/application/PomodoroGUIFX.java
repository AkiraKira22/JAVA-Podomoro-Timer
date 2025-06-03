package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class PomodoroGUIFX extends Application {

    private Label titleLabel;
    private Label artistLabel;
    private Label durationLabel;
    private Slider playbackSlider;

    private Button playPauseBtn;

    private MusicPlayer musicPlayer;
    private Boolean isPlaying = false;

    // Default directory to open FileChooser in
    File defaultDir = new File("src/main/resources/music");

    @Override
    public void start(Stage primaryStage) {
        musicPlayer = new MusicPlayer(this);

        titleLabel = new Label("Title: ");
        artistLabel = new Label("Artist: ");
        durationLabel = new Label("Duration: ");
        playbackSlider = new Slider(0, 1, 0);
        playbackSlider.setPrefWidth(300);

        playbackSlider.setOnMouseReleased(e -> {
            double value = playbackSlider.getValue();
            musicPlayer.seekTo(value);
        });

        HBox controls = getHBox(primaryStage);

        VBox root = new VBox(10, titleLabel, artistLabel, durationLabel, playbackSlider, controls);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 700, 250);
        primaryStage.setTitle("JavaFX Music Player");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox getHBox(Stage primaryStage) {
        playPauseBtn = new Button("Play");
        playPauseBtn.setPrefWidth(50);
        Button nextBtn = new Button("Next");
        nextBtn.setPrefWidth(65);
        Button prevBtn = new Button("Previous");
        prevBtn.setPrefWidth(65);
        Button muteBtn = new Button("Mute");
        muteBtn.setPrefWidth(50);
        Button loadSongBtn = new Button("Load Song");
        Button loadPlaylistBtn = new Button("Load Playlist");
        loadPlaylistBtn.setPrefWidth(95);
        Button createPlaylistBtn = new Button("Create Playlist");
        createPlaylistBtn.setPrefWidth(95);

        loadSongBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open MP3 File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));

            if (defaultDir.exists()) {
                fileChooser.setInitialDirectory(defaultDir);
            }

            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                musicPlayer.loadAndPlaySong(file);
                setPlayingState(true);
            }
        });

        createPlaylistBtn.setOnAction(e -> {
            MusicPlaylistDialogFX dialog = new MusicPlaylistDialogFX();
            dialog.show(primaryStage);
        });

        loadPlaylistBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Playlist File");
            File playlistDir = new File("src/main/resources/music/playlist");

            if (playlistDir.exists()) {
                fileChooser.setInitialDirectory(playlistDir);
            }

            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                musicPlayer.loadPlaylist(file);
            }
        });

        playPauseBtn.setOnAction(e -> {
            if (isPlaying) {
                musicPlayer.pauseSong();
                setPlayingState(false);
            }
            else {
                musicPlayer.resumeSong();
                setPlayingState(true);
            }
        });
        nextBtn.setOnAction(e -> musicPlayer.playNextSong());
        prevBtn.setOnAction(e -> musicPlayer.playPreviousSong());
        muteBtn.setOnAction(e -> musicPlayer.toggleMute());

        HBox controls = new HBox(10, playPauseBtn, prevBtn, nextBtn, muteBtn, loadSongBtn, createPlaylistBtn, loadPlaylistBtn);
        controls.setPadding(new Insets(10));
        return controls;
    }

    public void updateSongDisplay(Song song) {
        titleLabel.setText("Title: " + (song.getSongTitle() != null && !song.getSongTitle().isEmpty() ? song.getSongTitle() : "N/A"));
        artistLabel.setText("Artist: " + (song.getSongArtist() != null && !song.getSongArtist().isEmpty() ? song.getSongArtist() : "N/A"));
        durationLabel.setText("Duration: " + (song.getSongLength() != null ? song.getSongLength() : "--:--"));
    }

    public void setPlaybackSliderValue(double progress) {
        playbackSlider.setValue(progress);
    }

    private void setPlayingState(boolean playing) {
        isPlaying = playing;
        playPauseBtn.setText(isPlaying ? "Pause" : "Play");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
