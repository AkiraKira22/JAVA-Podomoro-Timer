/*package application;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MusicPlaylistDialogFX {

    private final List<String> songPaths = new ArrayList<>();

    // Default directory to open FileChooser in
    File defaultDir = new File("src/main/resources/music");

    public void show(Stage owner) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Create Playlist");
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(owner);

        VBox songListBox = new VBox(5);
        songListBox.setPadding(new Insets(10));
        ScrollPane scrollPane = new ScrollPane(songListBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);

        Button addButton = new Button("Add Song");
        Button saveButton = new Button("Save Playlist");

        addButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));
            fileChooser.setTitle("Select MP3 File");
            if (defaultDir.exists()) {
                fileChooser.setInitialDirectory(defaultDir);
            }
            File selected = fileChooser.showOpenDialog(dialogStage);
            if (selected != null) {
                String path = selected.getAbsolutePath();
                songPaths.add(path);
                Label songLabel = new Label(path);
                songLabel.setWrapText(true);
                songLabel.setStyle("-fx-border-color: black; -fx-padding: 5;");
                songListBox.getChildren().add(songLabel);
            }
        });

        saveButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Playlist");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File playlistDir = new File("src/main/resources/music/playlist");
            if (playlistDir.exists()) {
                fileChooser.setInitialDirectory(playlistDir);
            }
            File file = fileChooser.showSaveDialog(dialogStage);
            if (file != null) {
                if (!file.getName().endsWith(".txt")) {
                    file = new File(file.getAbsolutePath() + ".txt");
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    for (String path : songPaths) {
                        writer.write(path + "\n");
                    }
                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Success");
                    success.setHeaderText(null);
                    success.setContentText("Playlist saved successfully!");
                    success.showAndWait();
                    dialogStage.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        HBox buttons = new HBox(10, addButton, saveButton);
        buttons.setPadding(new Insets(10));

        VBox root = new VBox(10, scrollPane, buttons);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, 500, 400);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }
}
*/