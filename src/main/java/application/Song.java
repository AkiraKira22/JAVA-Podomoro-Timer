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
    private Mp3File mp3File;
    private final String filePath;

    public Song (String filePath) {
        this.filePath = filePath;
        try {
            mp3File = new Mp3File(filePath);
            songLength = convertToSongLengthFormat();
            
            // Use jaudiotagger library to create an audiofile obj to read mp3 file's info
            AudioFile audioFile = AudioFileIO.read(new File(filePath));
            Tag tag = audioFile.getTag();

            if (tag != null) {
                songTitle = tag.getFirst(FieldKey.TITLE);
                songArtist = tag.getFirst(FieldKey.ARTIST);
            }
            else {
                songTitle = "N/A";
                songArtist = "N/A";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String convertToSongLengthFormat() {
        long minutes = mp3File.getLengthInSeconds() / 60;
        long seconds = mp3File.getLengthInSeconds() %60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    // Getter
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
}
