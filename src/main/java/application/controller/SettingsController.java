package application.controller;

import application.MusicPlayer;
import application.model.SettingsModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

public class SettingsController {

    @FXML private MenuButton playlistMenuButton;

    @FXML private MenuItem preset1_1;
    @FXML private MenuItem preset25_5;
    @FXML private MenuItem preset50_10;

    @FXML private MenuItem cafePlaylist;
    @FXML private MenuItem groovyPlaylist;
    @FXML private MenuItem jazzPlaylist;
    @FXML private MenuItem lofiPlaylist;

    @FXML private CheckBox dndToggle;

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
            selectPreset("1_1");
        });

        preset25_5.setOnAction(e -> {
            settingsModel.setTimerPreset("25 + 5 minutes");
            if (presetListener != null) presetListener.onPresetSelected(25, 5);
            System.out.println("Preset selected: 25 + 5");
            selectPreset("25_5");
        });

        preset50_10.setOnAction(e -> {
            settingsModel.setTimerPreset("50 + 10 minutes");
            if (presetListener != null) presetListener.onPresetSelected(50, 10);
            System.out.println("Preset selected: 50 + 10");
            selectPreset("50_10");
        });

        // Handle playlist selections
        cafePlaylist.setOnAction(e -> selectPlaylist("cafe"));
        groovyPlaylist.setOnAction(e -> selectPlaylist("groovy"));
        jazzPlaylist.setOnAction(e -> selectPlaylist("jazz"));
        lofiPlaylist.setOnAction(e -> selectPlaylist("lofi"));
    }

    private void selectPreset(String preset) {
        // Remove tick from all
        preset1_1.setGraphic(null);
        preset25_5.setGraphic(null);
        preset50_10.setGraphic(null);

        // Add tick to selected
        switch (preset) {
            case "1_1" -> preset1_1.setGraphic(new Text("✓"));
            case "25_5" -> preset25_5.setGraphic(new Text("✓"));
            case "50_10" -> preset50_10.setGraphic(new Text("✓"));
        }
    }

    private void selectPlaylist(String name) {
        settingsModel.setPlaylistName(name);
        System.out.println("Playlist set to: " + name);

        // Remove tick from all
        cafePlaylist.setGraphic(null);
        groovyPlaylist.setGraphic(null);
        jazzPlaylist.setGraphic(null);
        lofiPlaylist.setGraphic(null);

        // Add tick to selected
        switch (name) {
            case "cafe" -> cafePlaylist.setGraphic(new Text("✓"));
            case "groovy" -> groovyPlaylist.setGraphic(new Text("✓"));
            case "jazz" -> jazzPlaylist.setGraphic(new Text("✓"));
            case "lofi" -> lofiPlaylist.setGraphic(new Text("✓"));
        }

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
}
