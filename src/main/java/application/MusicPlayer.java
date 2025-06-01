package application;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class MusicPlayer {

    private MediaPlayer mediaPlayer;
    private final ArrayList<Song> playlist = new ArrayList<>();
    private int currentSongIndex = 0;
    private boolean isMuted = false;

    private final PomodoroGUIX pomodoroGUIX;
    private Song currentSong;

    public MusicPlayer(PomodoroGUIX gui) {
        this.pomodoroGUIX = gui;
    }

    public void loadPlaylist(File playlistFile) {
        playlist.clear();
        currentSongIndex = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(playlistFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                playlist.add(new Song(line));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!playlist.isEmpty()) {
            playSong(playlist.get(currentSongIndex));
        }
    }

    public void loadAndPlaySong(File songFile) {
        Song song = new Song(songFile.getAbsolutePath());
        playlist.clear();
        playlist.add(song);
        currentSongIndex = 0;
        playSong(song);
    }

    public void playSong(Song song) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        currentSong = song;
        Media media = new Media(new File(song.getFilePath()).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        setupPlayerEvents();

        mediaPlayer.play();
        pomodoroGUIX.updateSongDisplay(song);
        pomodoroGUIX.enablePauseButton();
    }

    public void playNextSong() {
        if (playlist.isEmpty()) return;

        currentSongIndex = (currentSongIndex + 1) % playlist.size();
        playSong(playlist.get(currentSongIndex));
    }

    public void playPreviousSong() {
        if (playlist.isEmpty()) return;

        currentSongIndex = (currentSongIndex - 1 + playlist.size()) % playlist.size();
        playSong(playlist.get(currentSongIndex));
    }

    public void pauseSong() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void resumeSong() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    public void stopSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void toggleMute() {
        if (mediaPlayer != null) {
            isMuted = !isMuted;
            mediaPlayer.setMute(isMuted);
        }
    }

    public boolean isMuted() {
        return isMuted;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public void seekTo(double sliderPercentage) {
        if (mediaPlayer != null && mediaPlayer.getTotalDuration() != null) {
            double seconds = sliderPercentage * mediaPlayer.getTotalDuration().toSeconds();
            mediaPlayer.seek(Duration.seconds(seconds));
        }
    }

    private void setupPlayerEvents() {
        mediaPlayer.setOnEndOfMedia(this::playNextSong);

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            if (mediaPlayer.getTotalDuration() != null) {
                double progress = newTime.toSeconds() / mediaPlayer.getTotalDuration().toSeconds();
                pomodoroGUIX.setPlaybackSliderValue(progress);
            }
        });
    }
}
