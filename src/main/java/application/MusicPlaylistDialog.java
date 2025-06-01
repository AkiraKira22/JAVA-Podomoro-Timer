package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class MusicPlaylistDialog extends Stage {

    private final ArrayList<String> songPaths;
    private final VBox songContainer;

    public MusicPlaylistDialog(Stage owner) {
        songPaths = new ArrayList<>();

        setTitle("Create Playlist");
        setResizable(false);
        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setStyle("-fx-background-color: #000000;");

        songContainer = new VBox(5);
        songContainer.setPadding(new Insets(5));
        ScrollPane scrollPane = new ScrollPane(songContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        root.setCenter(scrollPane);

        HBox buttonsBox = new HBox(20);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPadding(new Insets(10, 0, 0, 0));

        Button addSongButton = new Button("Add");
        addSongButton.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        addSongButton.setOnAction(e -> openFileChooser());

        Button savePlaylistButton = new Button("Save");
        savePlaylistButton.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        savePlaylistButton.setOnAction(e -> savePlaylist());

        buttonsBox.getChildren().addAll(addSongButton, savePlaylistButton);
        root.setBottom(buttonsBox);

        Scene scene = new Scene(root, 400, 400);
        setScene(scene);
    }

    private void openFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select MP3 files");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 Files", "*.mp3"));
        fileChooser.setInitialDirectory(new File("src/main/resources/music"));

        File selectedFile = fileChooser.showOpenDialog(this);
        if (selectedFile != null) {
            String path = selectedFile.getPath();
            songPaths.add(path);

            Label pathLabel = new Label(path);
            pathLabel.setStyle("-fx-font-weight: bold; -fx-border-color: black; -fx-padding: 3 5 3 5;");
            songContainer.getChildren().add(pathLabel);
        }
    }

    private void savePlaylist() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Playlist");
        fileChooser.setInitialDirectory(new File("src/main/resources/music"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File selectedFile = fileChooser.showSaveDialog(this);
        if (selectedFile != null) {
            try {
                String fileName = selectedFile.getName();
                if (!fileName.toLowerCase().endsWith(".txt")) {
                    selectedFile = new File(selectedFile.getParentFile(), fileName + ".txt");
                }

                BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile));
                for (String path : songPaths) {
                    writer.write(path);
                    writer.newLine();
                }
                writer.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initOwner(this);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Successfully Created Playlist!");
                alert.showAndWait();

                close();

            } catch (Exception e) {
                e.printStackTrace();

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.initOwner(this);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to Save Playlist");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }
}
