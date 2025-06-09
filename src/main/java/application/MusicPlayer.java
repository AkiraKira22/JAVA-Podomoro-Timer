package application;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.ArrayList;

public class MusicPlayer {
    private static MusicPlayer instance;

    public MediaPlayer mediaPlayer;
    public final ArrayList<Song> playlist = new ArrayList<>();
    private int currentSongIndex = 0;
    public boolean isMuted = false;

    private double volume = 50; // volume range 0-100
    private double previousVolume = 50; // to store volume before mute

    private MusicPlayer() {
    }

    public static MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }

    public void setPresetPlaylist(String playlistName) {
        if (mediaPlayer != null) {
            mediaPlayer.stop(); // stop any current playback immediately
        }

        playlist.clear();
        currentSongIndex = 0;

        String basePath = "src/main/resources/music/";

        switch (playlistName.toLowerCase()) {
            case "cafe":
                playlist.add(new Song(basePath + "cafe.mp3"));
                break;
            case "groovy":
                playlist.add(new Song(basePath + "groovy.mp3"));
                break;
            case "jazz":
                playlist.add(new Song(basePath + "jazz.mp3"));
                break;
            case "lofi":
                playlist.add(new Song(basePath + "lofi.mp3"));
                break;
            default:
                System.out.println("Unknown preset playlist: " + playlistName);
                return;
        }
        //playSong(playlist.getFirst());
    }

    public void playSong(Song song) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }

        Media media = new Media(new File(song.getFilePath()).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        setupPlayerEvents();
        mediaPlayer.play();

        mediaPlayer.setVolume(volume / 100.0); // convert 0-100 to 0-1
        mediaPlayer.setMute(isMuted);
    }

    public void playCurrentSong() {
        if (!playlist.isEmpty()) {
            playSong(playlist.get(currentSongIndex));
        }
    }

    public void playNextSong() {
        if (playlist.isEmpty()) return;
        currentSongIndex = (currentSongIndex + 1) % playlist.size();
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
        } else if (!playlist.isEmpty()) {
            playSong(playlist.get(currentSongIndex));
        }
    }

    public void setVolume(double volume) {
        if (volume < 0) volume = 0;
        if (volume > 100) volume = 100;

        this.volume = volume;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume / 100.0);
            // isMuted = (volume == 0)
        }
    }

    public double getVolume() {
        return volume;
    }

    public void toggleMute() {
        toggleMute(!isMuted);
    }

    public void toggleMute(boolean mute) {
        if (mute) {
            if (!isMuted) {
                previousVolume = volume > 0 ? volume : 50;
                setVolume(0);
            }
        }
        else {
            if (isMuted) {
                setVolume(previousVolume);
            }
        }
        isMuted = mute;
        if (mediaPlayer != null) {
            mediaPlayer.setMute(mute);
        }
    }

    public boolean isMuted() {
        return isMuted;
    }

    private void setupPlayerEvents() {
        mediaPlayer.setOnEndOfMedia(this::playNextSong);
        // Removed playback slider update since no GUI
    }
}
