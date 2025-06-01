package application.controller;

import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ToggleButton;

public class SettingsController {

    @FXML
    private MenuButton timerMenu;

    @FXML
    private MenuButton musicMenu;

    @FXML
    private ToggleButton dndToggle;

    @FXML
    public void initialize() {
        // Initialize timer presets and other settings
    }

    @FXML
    private void handleTimerPresetSelection() {
        // Handle timer preset selection
    }

    @FXML
    private void handleMusicMenu() {
        // Open music window
    }

    @FXML
    private void handleDNDToggle() {
        // Enable or disable Do Not Disturb feature
    }
}