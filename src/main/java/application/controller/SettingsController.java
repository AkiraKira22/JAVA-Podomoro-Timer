package application.controller;

import application.MusicPlayer;
import application.model.SettingsModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SettingsController {

    @FXML private CheckBox dndToggle;

    @FXML private MenuButton playlistMenuButton;

    @FXML private MenuItem preset1_1;
    @FXML private MenuItem preset25_5;
    @FXML private MenuItem preset50_10;

    @FXML private MenuItem cafePlaylist;
    @FXML private MenuItem groovyPlaylist;
    @FXML private MenuItem jazzPlaylist;
    @FXML private MenuItem lofiPlaylist;

    @FXML private MenuItem dndMenuItem;

    private final SettingsModel settingsModel = new SettingsModel();

    // Listener interface for preset selection callback
    public interface TimerPresetListener {
        void onPresetSelected(int focusMinutes, int restMinutes);
    }

    private TimerPresetListener presetListener;

    public void setTimerPresetListener(TimerPresetListener listener) {
        this.presetListener = listener;
    }

    @FXML
    public void initialize() {
        // Handle Preset selections
        preset1_1.setOnAction(e -> {
            settingsModel.setTimerPreset("1 + 1 minutes");
            if (presetListener != null) presetListener.onPresetSelected(1, 1);
            System.out.println("Preset selected: 1 + 1");
        });

        preset25_5.setOnAction(e -> {
            settingsModel.setTimerPreset("25 + 5 minutes");
            if (presetListener != null) presetListener.onPresetSelected(25, 5);
            System.out.println("Preset selected: 25 + 5");
        });

        preset50_10.setOnAction(e -> {
            settingsModel.setTimerPreset("50 + 10 minutes");
            if (presetListener != null) presetListener.onPresetSelected(50, 10);
            System.out.println("Preset selected: 50 + 10");
        });

        // Handle playlist selections
        cafePlaylist.setOnAction(e -> setPlaylist("cafe"));
        groovyPlaylist.setOnAction(e -> setPlaylist("groovy"));
        jazzPlaylist.setOnAction(e -> setPlaylist("jazz"));
        lofiPlaylist.setOnAction(e -> setPlaylist("lofi"));

        // DND Menu Item toggles CheckBox and model
        dndMenuItem.setOnAction(e -> {
            boolean newState = !dndToggle.isSelected();
            dndToggle.setSelected(newState);
            settingsModel.setDndEnabled(newState);
            System.out.println("DND toggled via menu: " + newState);
        });
    }

    private void setPlaylist(String name) {
        settingsModel.setPlaylistName(name);
        System.out.println("Playlist set to: " + name);

        if (playlistChangeListener != null) {
            playlistChangeListener.onPlaylistChanged(name);
        }
    }

    public interface PlaylistChangeListener {
        void onPlaylistChanged(String playlistName);
    }

    private PlaylistChangeListener playlistChangeListener;

    public void setPlaylistChangeListener(PlaylistChangeListener listener) {
        this.playlistChangeListener = listener;
    }

    @FXML
    private void handleDNDToggle() {
        settingsModel.setDndEnabled(dndToggle.isSelected());
        System.out.println("DND toggled: " + dndToggle.isSelected());
    }
}
