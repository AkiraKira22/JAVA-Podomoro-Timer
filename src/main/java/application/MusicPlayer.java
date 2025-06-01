package application;

import javazoom.jl.player.advanced.PlaybackListener;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.AdvancedPlayer;

import javafx.application.Platform;

import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class MusicPlayer extends PlaybackListener {
    private static final Object playSignal = new Object();

    private final MusicPlayerGUI musicPlayerGUI;

    private Song currentSong;
    public Song getCurrentSong() { return currentSong; }

    private ArrayList<Song> playlist;
    private int currentSongIndex;
    private AdvancedPlayer advancedPlayer;

    private boolean isPaused;
    private boolean isMuted = false;
    private FloatControl volumeControl;
    private float initialVolume;

    private int currentFrame;
    public void setCurrentFrame(int frame) { currentFrame = frame; }

    private int currentTimeInMilliseconds;
    public void setCurrentTimeInMilliseconds(int timeInMilliseconds) {
        currentTimeInMilliseconds = timeInMilliseconds;
    }

    public MusicPlayer(MusicPlayerGUI musicPlayerGUI) {
        this.musicPlayerGUI = musicPlayerGUI;
    }

    private void initializeVolumeControl() {
        try {
            if (advancedPlayer != null) {
                Field sourceField = AdvancedPlayer.class.getDeclaredField("source");
                sourceField.setAccessible(true);
                Object source = sourceField.get(advancedPlayer);
                if (source instanceof SourceDataLine sourceLine) {
                    if (sourceLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                        volumeControl = (FloatControl) sourceLine.getControl(FloatControl.Type.MASTER_GAIN);
                        initialVolume = volumeControl.getValue();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadSong(Song song) {
        currentSong = song;
        volumeControl = null;
        if (currentSong != null) {
            currentFrame = 0;
            currentTimeInMilliseconds = 0;
            Platform.runLater(() -> {
                musicPlayerGUI.updateSongMetadata(currentSong);
                musicPlayerGUI.updateSliderMax(currentSong);
                musicPlayerGUI.updateSliderValue(0);
                musicPlayerGUI.enablePauseButton();
            });
            playCurrentSong();
        }
    }

    public void loadPlaylist(File playlistFile) {
        playlist = new ArrayList<>();
        volumeControl = null;
        currentSongIndex = 0;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(playlistFile))) {
            String songPath;
            while ((songPath = bufferedReader.readLine()) != null) {
                Song song = new Song(songPath);
                playlist.add(song);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!playlist.isEmpty()) {
            musicPlayerGUI.updateSliderValue(0);
            currentTimeInMilliseconds = 0;
            currentSong = playlist.getFirst();
            currentFrame = 0;

            Platform.runLater(() -> {
                musicPlayerGUI.enablePauseButton();
                musicPlayerGUI.updateSongMetadata(currentSong);
                musicPlayerGUI.updateSliderMax(currentSong);
            });

            playCurrentSong();
        }
    }

    public void pauseSong() {
        if (advancedPlayer != null) {
            isPaused = true;
            stopSong();
        }
    }

    public synchronized void stopSong() {
        if (advancedPlayer != null) {
            try {
                advancedPlayer.close();
            } catch (Exception ignored) {}
            advancedPlayer = null;
        }
        isPaused = false;
    }

    public synchronized void playCurrentSong() {
        stopSong();

        if (currentSong == null) return;

        try {
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);

            initializeVolumeControl();

            startMusicThread();
            startPlaybackSliderThread();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void seekToFrame(int frame) {
        boolean wasPlaying = (advancedPlayer != null && !isPaused);
        stopSong();
        currentFrame = frame;
        currentTimeInMilliseconds = (int) (frame / currentSong.getFrameRatePerMilliseconds());
        if (wasPlaying) {
            playCurrentSong();
        }
    }

    public void playNextSong() {
        if (playlist == null || playlist.isEmpty()) return;

        currentSongIndex++;
        if (currentSongIndex >= playlist.size()) {
            currentSongIndex = 0;
        }

        currentSong = playlist.get(currentSongIndex);
        currentFrame = 0;
        currentTimeInMilliseconds = 0;

        Platform.runLater(() -> {
            musicPlayerGUI.updateSongMetadata(currentSong);
            musicPlayerGUI.updateSliderMax(currentSong);
            musicPlayerGUI.updateSliderValue(0);
            musicPlayerGUI.enablePauseButton();
        });

        playCurrentSong();
    }

    private void startMusicThread() {
        new Thread(() -> {
            try {
                if (isPaused) {
                    synchronized (playSignal) {
                        isPaused = false;
                        playSignal.notify();
                    }
                    advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                } else {
                    advancedPlayer.play();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startPlaybackSliderThread() {
        new Thread(() -> {
            try {
                while (advancedPlayer != null && !isPaused) {
                    currentTimeInMilliseconds++;
                    int frame = (int) (currentTimeInMilliseconds * currentSong.getFrameRatePerMilliseconds());
                    Platform.runLater(() -> musicPlayerGUI.updateSliderValue(frame));
                    Thread.sleep(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void playbackStarted(PlaybackEvent e) {
        System.out.println("Playback started");
        if (volumeControl == null) {
            initializeVolumeControl();
        }
    }

    public void toggleMute() {
        if (volumeControl == null) {
            initializeVolumeControl();
        }

        if (volumeControl != null) {
            if (isMuted) {
                volumeControl.setValue(initialVolume);
            } else {
                volumeControl.setValue(volumeControl.getMinimum());
            }
            isMuted = !isMuted;
        } else {
            System.err.println("Volume control not available.");
        }
    }

    public boolean isMuted() {
        return isMuted;
    }

    @Override
    public void playbackFinished(PlaybackEvent e) {
        System.out.println("Playback finished");
        if (!isPaused) {
            playNextSong();
        }
    }
}
