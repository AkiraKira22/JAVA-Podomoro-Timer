package application.controller;

import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

import application.MusicPlayerGUIFX;

public class SettingsController {

    @FXML private CheckBox dndToggle;
    @FXML private Button openMusic;
    @FXML private MenuButton timerMenuButton;
    @FXML private MenuItem presetTest;
    @FXML private MenuItem preset25_5;
    @FXML private MenuItem preset50_10;

    private TimerPresetListener presetListener;

    // NEW: Persistent instance of MusicPlayerGUIFX
    private final MusicPlayerGUIFX musicPlayerWindow = new MusicPlayerGUIFX();

    public interface TimerPresetListener {
        void onPresetSelected(int focusMinutes, int restMinutes);
    }

    public void setTimerPresetListener(TimerPresetListener listener) {
        this.presetListener = listener;
    }

    @FXML
    public void initialize() {
        presetTest.setOnAction(e -> {
            if (presetListener != null) presetListener.onPresetSelected(1, 1);
        });
        preset25_5.setOnAction(e -> {
            if (presetListener != null) {
                presetListener.onPresetSelected(25, 5);
            }
        });
        preset50_10.setOnAction(e -> {
            if (presetListener != null) {
                presetListener.onPresetSelected(50, 10);
            }
        });
    }

    @FXML
    private void handleMusicMenu() {
        musicPlayerWindow.showPlayerWindow();
    }

    @FXML
    private void handleDNDToggle() {
        // Optional: handle "Do Not Disturb" logic here
    }
}
