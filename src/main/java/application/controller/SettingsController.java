package application.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;

import application.MusicPlayerGUIFX;

public class SettingsController {

    // @FXML private MenuButton timerMenu;
    // @FXML private MenuButton musicMenu;
    @FXML private CheckBox dndToggle;
    @FXML private Button openMusic;
    @FXML private MenuButton timerMenuButton;
    @FXML private MenuItem presetTest;
    @FXML private MenuItem preset25_5;
    @FXML private MenuItem preset50_10;

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
        // Only open one music player window at a time
        if (MainController.musicPlayerStage != null && MainController.musicPlayerStage.isShowing()) {
            MainController.musicPlayerStage.toFront();
            MainController.musicPlayerStage.requestFocus();
            return;
        }
        MusicPlayerGUIFX musicPlayerWindow = new MusicPlayerGUIFX();
        MainController.musicPlayerStage = new Stage();
        musicPlayerWindow.start(MainController.musicPlayerStage);
        MainController.musicPlayerStage.setOnCloseRequest(e -> MainController.musicPlayerStage = null);
    }

    @FXML
    private void handleDNDToggle() {
        // Enable or disable Do Not Disturb feature
    }
}