package application.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;
//import javafx.scene.control.ToggleButton;
import javafx.scene.control.MenuItem;

public class SettingsController {

    @FXML private MenuButton timerMenu;
    @FXML private MenuButton musicMenu;
    @FXML private CheckMenuItem dndToggle;
    @FXML private MenuItem presetA;
    @FXML private MenuItem presetB;

    private TimerPresetListener presetListener;

    public interface TimerPresetListener {
        void onPresetSelected(int focusMinutes, int restMinutes);
    }

    public void setTimerPresetListener(TimerPresetListener listener) {
        this.presetListener = listener;
    }

    @FXML
    public void initialize() {
        presetA.setOnAction(e -> {
            if (presetListener != null) presetListener.onPresetSelected(25, 5);
        });
        presetB.setOnAction(e -> {
            if (presetListener != null) presetListener.onPresetSelected(50, 10);
        });
    }

    /*@FXML
    private void handleTimerPresetSelection() {
        // Handle timer preset selection
    }*/

    @FXML
    private void handleMusicMenu() {
        // Open music window
    }

    @FXML
    private void handleDNDToggle() {
        // Enable or disable Do Not Disturb feature
    }
}