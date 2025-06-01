package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MusicPlayerGUIFX extends Application {

    private Label titleLabel;
    private Label artistLabel;
    private Label durationLabel;
    private Slider playbackSlider;

    private Button pauseBtn;

    private MusicPlayer musicPlayer;

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

        Scene scene = new Scene(root, 520, 240);
        primaryStage.setTitle("JavaFX Music Player");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox getHBox(Stage primaryStage) {
        Button loadSongBtn = new Button("Load Song");
        Button loadPlaylistBtn = new Button("Load Playlist");
        Button createPlaylistBtn = new Button("Create Playlist");
        Button playBtn = new Button("Play");
        pauseBtn = new Button("Pause");
        Button nextBtn = new Button("Next");
        Button prevBtn = new Button("Previous");
        Button muteBtn = new Button("Mute");

        // Default directory to open FileChooser in
        File defaultDir = new File("D:/NTOU/1132/JAVA/~FinalProject/JAVA-Podomoro-Timer/src/main/resources/music");

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
            }
        });

        createPlaylistBtn.setOnAction(e -> {
            MusicPlaylistDialogFX dialog = new MusicPlaylistDialogFX();
            dialog.show(primaryStage);
        });

        loadPlaylistBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Playlist File");

            if (defaultDir.exists()) {
                fileChooser.setInitialDirectory(defaultDir);
            }

            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                musicPlayer.loadPlaylist(file);
            }
        });

        playBtn.setOnAction(e -> musicPlayer.resumeSong());
        pauseBtn.setOnAction(e -> musicPlayer.pauseSong());
        nextBtn.setOnAction(e -> musicPlayer.playNextSong());
        prevBtn.setOnAction(e -> musicPlayer.playPreviousSong());
        muteBtn.setOnAction(e -> musicPlayer.toggleMute());

        HBox controls = new HBox(10, playBtn, pauseBtn, prevBtn, nextBtn, muteBtn, loadSongBtn, createPlaylistBtn, loadPlaylistBtn);
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

    public void enablePauseButton() {
        if (pauseBtn != null) {
            pauseBtn.setDisable(false);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
