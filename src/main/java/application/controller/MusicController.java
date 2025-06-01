package application.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.List;

public class MusicController {

    @FXML
    private ListView<String> musicListView;

    @FXML
    private Button playPauseButton;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    @FXML
    public void initialize() {
        loadMusicFiles();
    }

    private void loadMusicFiles() {
        // Load music files from a directory (e.g., "music" folder in the project)
        File musicDir = new File("music");
        File[] musicFiles = musicDir.listFiles((dir, name) -> name.endsWith(".mp3"));

        if (musicFiles != null) {
            for (File musicFile : musicFiles) {
                musicListView.getItems().add(musicFile.getName());
            }
        }
    }

    @FXML
    public void playPauseMusic() {
        if (isPlaying) {
            mediaPlayer.pause();
            playPauseButton.setText("Play");
        } else {
            String selectedMusic = musicListView.getSelectionModel().getSelectedItem();
            if (selectedMusic != null) {
                playMusic(selectedMusic);
                playPauseButton.setText("Pause");
            }
        }
        isPlaying = !isPlaying;
    }

    private void playMusic(String musicFileName) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        Media media = new Media(new File("music/" + musicFileName).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
    }
}