package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;

public class MusicPlayerGUI {
    private final Stage primaryStage;
    private Label songTitle, songArtist;
    private Slider playbackSlider;
    private Button playButton, pauseButton, muteButton;
    private MusicPlayer musicPlayer;
    private FileChooser fileChooser;

    public MusicPlayerGUI(Stage primaryStage) {
        this.primaryStage = primaryStage;
        buildUI();
    }

    private void buildUI() {
        musicPlayer = new MusicPlayer(this);
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("src/main/resources/music"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 files", "*.mp3"));

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: black;");

        // Top Menu
        MenuBar menuBar = new MenuBar();
        Menu songMenu = new Menu("Song");
        MenuItem loadSong = new MenuItem("Load Song");
        loadSong.setOnAction(e -> loadSong(primaryStage));
        songMenu.getItems().add(loadSong);

        Menu playlistMenu = new Menu("Playlist");
        MenuItem createPlaylist = new MenuItem("Create Playlist");
        createPlaylist.setOnAction(e -> new MusicPlaylistDialog(primaryStage).show());
        MenuItem loadPlaylist = new MenuItem("Load Playlist");
        loadPlaylist.setOnAction(e -> loadPlaylist(primaryStage));
        playlistMenu.getItems().addAll(createPlaylist, loadPlaylist);

        menuBar.getMenus().addAll(songMenu, playlistMenu);
        root.setTop(menuBar);

        // Center content
        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(10));

        ImageView songImage = new ImageView(new Image("src/main/resources/record.png"));
        songImage.setFitWidth(200);
        songImage.setFitHeight(200);
        centerBox.getChildren().add(songImage);

        songTitle = new Label("Song Title");
        songTitle.setTextFill(Color.WHITE);
        songTitle.setFont(new Font("Arial", 24));

        songArtist = new Label("Artist");
        songArtist.setTextFill(Color.WHITE);
        songArtist.setFont(new Font("Arial", 20));

        centerBox.getChildren().addAll(songTitle, songArtist);

        playbackSlider = new Slider();
        playbackSlider.setMin(0);
        playbackSlider.setMax(100);
        playbackSlider.setValue(0);
        playbackSlider.setPrefWidth(300);
        playbackSlider.setOnMousePressed(e -> musicPlayer.pauseSong());
        playbackSlider.setOnMouseReleased(e -> {
            double value = playbackSlider.getValue();
            musicPlayer.seekToFrame((int) value);
            enablePauseButton();
        });

        centerBox.getChildren().add(playbackSlider);
        root.setCenter(centerBox);

        // Bottom Controls
        HBox controls = new HBox(10);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(20));
        controls.setStyle("-fx-background-color: transparent;");

        muteButton = createImageButton("src/main/resources/unmute.png");
        muteButton.setOnAction(e -> {
            musicPlayer.toggleMute();
            String icon;
            if (musicPlayer.isMuted()) {
                icon = "src/main/resources/mute.png";
            }
            else {
                icon = "src/main/resources/unmute.png";
            }
            muteButton.setGraphic(new ImageView(new Image("file:" + icon)));
        });

        playButton = createImageButton("src/main/resources/play.png");
        playButton.setOnAction(e -> {
            musicPlayer.playCurrentSong();
            enablePauseButton();
        });

        pauseButton = createImageButton("src/main/resources/pause.png");
        pauseButton.setVisible(false);
        pauseButton.setOnAction(e -> {
            musicPlayer.pauseSong();
            enablePlayButton();
        });

        Button nextButton = createImageButton("src/main/resources/next.png");
        nextButton.setOnAction(e -> musicPlayer.playNextSong());

        controls.getChildren().addAll(muteButton, playButton, pauseButton, nextButton);
        root.setBottom(controls);

        Scene scene = new Scene(root, 400, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Music Player");
        primaryStage.setResizable(false);
    }

    public void show() {
        primaryStage.show();
    }

    private void loadSong(Stage stage) {
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            Song song = new Song(file.getPath());
            musicPlayer.loadSong(song);
        }
    }

    private void loadPlaylist(Stage stage) {
        FileChooser playlistChooser = new FileChooser();
        playlistChooser.setInitialDirectory(new File("src/main/resources/music"));
        playlistChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Playlist Files", "*.txt"));
        File file = playlistChooser.showOpenDialog(stage);
        if (file != null) {
            musicPlayer.stopSong();
            musicPlayer.loadPlaylist(file);
        }
    }

    private Button createImageButton(String resourcePath) {
        Image img = new Image(Objects.requireNonNull(getClass().getResourceAsStream(resourcePath)));
        ImageView view = new ImageView(img);
        view.setFitHeight(40);
        view.setFitWidth(40);
        Button button = new Button("", view);
        button.setStyle("-fx-background-color: transparent;");
        return button;
    }

    public void updateSliderValue(int frame) {
        playbackSlider.setValue(frame);
    }

    public void updateSliderMax(Song song) {
        playbackSlider.setMax(song.getMp3File().getFrameCount());
    }

    public void updateSongMetadata(Song song) {
        songTitle.setText(song.getSongTitle());
        songArtist.setText(song.getSongArtist());
    }

    public void enablePauseButton() {
        playButton.setVisible(false);
        pauseButton.setVisible(true);
    }

    public void enablePlayButton() {
        playButton.setVisible(true);
        pauseButton.setVisible(false);
    }
}
