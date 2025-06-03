package application.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SettingsModel {
    private final StringProperty timerPreset;
    private boolean dndEnabled;

    public SettingsModel() {
        this.timerPreset = new SimpleStringProperty("25 + 5 minutes"); // Default preset
        this.dndEnabled = false; // DND is disabled by default
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

    public boolean isDndEnabled() {
        return dndEnabled;
    }

    public void setDndEnabled(boolean dndEnabled) {
        this.dndEnabled = dndEnabled;
    }
}