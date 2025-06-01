package application;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import com.mpatric.mp3agic.Mp3File;

import java.io.File;

public class Song {
    private String songTitle;
    private String songArtist;
    private String songLength;
    private final String filePath;
    private Mp3File mp3File;
    private double frameRatePerMilliseconds;

    public Song(String filePath) {
        this.filePath = filePath;
        try {
            mp3File = new Mp3File(filePath);
            if (mp3File.getFrameCount() > 0 && mp3File.getLengthInMilliseconds() > 0) {
                frameRatePerMilliseconds = (double) mp3File.getFrameCount() / mp3File.getLengthInMilliseconds();
            }
            else {
                frameRatePerMilliseconds = 0.0;
            }
            songLength = convertToSongLengthFormat();

            File audioFile = new File(filePath);
            if (audioFile.exists()) {
                AudioFile audio = AudioFileIO.read(audioFile);
                Tag tag = audio.getTag();

                if (tag != null) {
                    songTitle = tag.getFirst(FieldKey.TITLE);
                    songArtist = tag.getFirst(FieldKey.ARTIST);

                    // Use filename if metadata is missing
                    if (songTitle == null || songTitle.isEmpty()) {
                        songTitle = audioFile.getName().replace(".mp3", "");
                    }
                    if (songArtist == null || songArtist.isEmpty()) {
                        songArtist = "Unknown Artist";
                    }
                }
                else {
                    songTitle = audioFile.getName().replace(".mp3", "");
                    songArtist = "Unknown Artist";
                }
            }
            else {
                songTitle = "File not found";
                songArtist = "N/A";
            }
        } catch (Exception e) {
            e.printStackTrace();
            songTitle = "N/A";
            songArtist = "N/A";
            songLength = "00:00";
            mp3File = null;
            frameRatePerMilliseconds = 0.0;
        }
    }

    private String convertToSongLengthFormat() {
        if (mp3File == null) return "00:00";

        long totalSeconds = mp3File.getLengthInSeconds();
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getSongArtist() {
        return songArtist;
    }

    public String getSongLength() {
        return songLength;
    }

    public String getFilePath() {
        return filePath;
    }

    public Mp3File getMp3File() {
        return mp3File;
    }

    public double getFrameRatePerMilliseconds() {
        return frameRatePerMilliseconds;
    }
}
