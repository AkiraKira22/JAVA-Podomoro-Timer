package application.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SettingsModel {
    private final StringProperty timerPreset;
    private final StringProperty playlistName;
    private boolean dndEnabled;

    public SettingsModel() {
        this.timerPreset = new SimpleStringProperty("25 + 5 minutes");
        this.playlistName = new SimpleStringProperty("lofi"); // Default
        this.dndEnabled = false;
    }

    public String getTimerPreset() {
        return timerPreset.get();
    }

    public void setTimerPreset(String timerPreset) {
        this.timerPreset.set(timerPreset);
    }

    public StringProperty timerPresetProperty() {
        return timerPreset;
    }

    public String getPlaylistName() {
        return playlistName.get();
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName.set(playlistName);
    }

    public StringProperty playlistNameProperty() {
        return playlistName;
    }

    public boolean isDndEnabled() {
        return dndEnabled;
    }

    public void setDndEnabled(boolean dndEnabled) {
        this.dndEnabled = dndEnabled;
    }
}
