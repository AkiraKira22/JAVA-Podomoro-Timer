package application.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;
//import javafx.scene.control.ToggleButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import application.MusicPlayerGUIFX;

public class SettingsController {

    @FXML private MenuButton timerMenu;
    @FXML private MenuButton musicMenu;
    @FXML private CheckMenuItem dndToggle;
    @FXML private MenuItem presetTest;
    @FXML private MenuItem presetA;
    @FXML private MenuItem presetB;
    @FXML private MenuItem openMusic;

    private TimerPresetListener presetListener;

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
        presetA.setOnAction(e -> {
            if (presetListener != null) presetListener.onPresetSelected(25, 5);
        });
        presetB.setOnAction(e -> {
            if (presetListener != null) presetListener.onPresetSelected(50, 10);
        });
    }


    @FXML
    private void handleMusicMenu() {
        // Open music window
        MusicPlayerGUIFX musicPlayerWindow = new MusicPlayerGUIFX();
        Stage stage = new Stage();
        musicPlayerWindow.start(stage);
    }

    @FXML
    private void handleDNDToggle() {
        // Enable or disable Do Not Disturb feature
    }
}