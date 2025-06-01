package application;

// Store song details
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;
import javax.swing.SwingUtilities;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;

public class MusicPlayer extends PlaybackListener{
    // Update isPaused more accurately
    private static final Object playSignal = new Object();

    //reference to update the GUI in this class
    private MusicPlayerGUI musicPlayerGUI;

    private Song currentSong;
    public Song getCurrentSong() {
        return currentSong;
    }

    private ArrayList<Song> playlist;

    // Get index of the song currently playing
    private int currentSongIndex;

    // Use JLayer library to create an AdvancedPlayer obj which will handle playing the music
    private AdvancedPlayer advancedPlayer;

    // Pause bool flag
    private boolean isPaused;

    // Mute bool flag
    private boolean isMuted = false;
    private FloatControl volumeControl;
    private float initialVolume;

    // Stores the last frame when the playback is stopped (used for pausing and resuming)
    private int currentFrame;
    public void setCurrentFrame(int frame) {
        currentFrame = frame;
    }
    
    // Track how many milliseconds has passed to update the slider
    private int currentTimeInMilliseconds;
    public void setCurrentTimeInMilliseconds( int timeInMilliseconds) {
        currentTimeInMilliseconds = timeInMilliseconds;
    }

    private void initializeVolumeControl() {
        try {
            Field sourceField = AdvancedPlayer.class.getDeclaredField("source");
            sourceField.setAccessible(true);
            Object source = sourceField.get(advancedPlayer);
            
            if (source instanceof SourceDataLine) {
                SourceDataLine sourceLine = (SourceDataLine) source;
                if (sourceLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    volumeControl = (FloatControl) sourceLine.getControl(FloatControl.Type.MASTER_GAIN);
                    initialVolume = volumeControl.getValue();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void toggleMute() {
        if (volumeControl == null) {
            initializeVolumeControl();
        }

        if (volumeControl != null) {
            if (isMuted) {
                volumeControl.setValue(initialVolume);
            }
            else {
                volumeControl.setValue(volumeControl.getMinimum());
            }
            isMuted = !isMuted;
        }
        else {
            System.err.println("Volume control not available.");
        }
    }

    public boolean isMuted() {
        return isMuted;
    }

    // Constructor
    public MusicPlayer(MusicPlayerGUI musicPlayerGUI) {
        this.musicPlayerGUI = musicPlayerGUI;
    }

    public void loadSong(Song song) {
        currentSong = song;
        volumeControl = null;
        if (currentSong != null) {
            playCurrentSong();
        }
    }

    public void loadPlaylist(File playlistFile) {
        playlist = new ArrayList<>();
        volumeControl = null;
        currentSongIndex = 0;

        // Store the paths from the text file into the playlist array list
        try {
            FileReader fileReader = new FileReader(playlistFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Reach each line from the text file and store the text into the songPath variable
            String songPath;
            while ((songPath = bufferedReader.readLine()) != null) {
                // Create song object based on song path
                Song song = new Song(songPath);

                // Add to playlist array list
                playlist.add(song);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!playlist.isEmpty()) {
            // Reset playback slider
            musicPlayerGUI.setPlaybackSliderValue(0);
            currentTimeInMilliseconds = 0;

            // Get current playing song index
            currentSong = playlist.get(currentSongIndex);

            // Update current song to the first song in the playlist
            currentSong = playlist.getFirst();

            // Start from the beginning frame
            currentFrame = 0;

            // Update GUI
            musicPlayerGUI.enablePauseButton();
            musicPlayerGUI.updateSongTitleAndArtist(currentSong);
            musicPlayerGUI.updatePlaybackSlider(currentSong);

            // Play current song
            playCurrentSong();
        }
    }

    public void pauseSong() {
        if (advancedPlayer != null) {
            isPaused = true;
            stopSong();
        }
    }

    public void stopSong() {
        if (advancedPlayer != null) {
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }

    public void playCurrentSong() {
        if (currentSong == null) return;
        
        try {
            // read mp3 audio data
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            // Create new advanced player
            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);

            // Initialize volume control
            initializeVolumeControl();

            // Start music
            startMusicThread();

            // Start playback slider thread
            startPlaybackSliderThread();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playNextSong() {
        if (playlist == null || playlist.isEmpty()) return;

        // PLay next song, loop if at the end
        currentSongIndex++;
        if (currentSongIndex >= playlist.size()) {
            currentSongIndex = 0;
        }

        currentSong = playlist.get(currentSongIndex);
        currentFrame = 0;
        currentTimeInMilliseconds = 0;

        // Update GUI
        SwingUtilities.invokeLater(() -> {
            musicPlayerGUI.updateSongTitleAndArtist(currentSong);
            musicPlayerGUI.updatePlaybackSlider(currentSong);
            musicPlayerGUI.setPlaybackSliderValue(0);
            musicPlayerGUI.enablePauseButton();
        });

        playCurrentSong();
    }

    // Thread that will handle playing the music
    private void startMusicThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (isPaused) {
                        synchronized(playSignal) {
                            isPaused = false;

                            // Notify other threads to continue
                            playSignal.notify();
                        }

                        // Resume music from last frame
                        advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                    }
                    else {
                        advancedPlayer.play();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Thread that will handle updating the slider
    private void startPlaybackSliderThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isPaused) {
                    try {
                        // Wait till notifies by other thread to continue
                        synchronized(playSignal) {
                            playSignal.wait();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                while(!isPaused) {
                    try {
                        // Increment current time
                        currentTimeInMilliseconds++;

                        // calculate into frame value
                        int calculateFrame = (int)((double) currentTimeInMilliseconds * 2.08 * currentSong.getFrameRatePerMilliseconds());

                        // Update GUI
                        musicPlayerGUI.setPlaybackSliderValue(calculateFrame);

                        // mimic 1 millisecond using thread.sleep
                        Thread.sleep(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void playbackStarted(PlaybackEvent e) {
        // Called at the start of song
        System.out.println("Playback started");
        if (volumeControl == null) {
            initializeVolumeControl();
        }
    }
    
    @Override
    public void playbackFinished(PlaybackEvent e) {
        // Called at the end of song
        System.out.println("Playback finished");
        if (isPaused) {
            currentFrame += (int) ((double) e.getFrame() * currentSong.getFrameRatePerMilliseconds());
        }
        else {
            playNextSong(); // Automatically play the next song
        }
    }
}
