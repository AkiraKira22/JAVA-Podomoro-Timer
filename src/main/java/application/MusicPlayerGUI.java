package application;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MusicPlayerGUI extends JFrame {

    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;

    private final MusicPlayer musicPlayer;

    // Allows using file explorer
    private final JFileChooser jFileChooser;

    private JLabel songTitle, songArtist;
    private JPanel playbackButtons;

    private JSlider playbackSlider;

    public MusicPlayerGUI() {
        super("Music Player");

        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        // Change frame color
        getContentPane().setBackground(FRAME_COLOR);

        musicPlayer = new MusicPlayer(this);
        jFileChooser = new JFileChooser();

        // Set default path for file explorer
        jFileChooser.setCurrentDirectory(new File("src/main/resources/music"));

        // Filter to only show mp3 files
        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "MP3"));

        addGuiComponents();
    }

    private void addGuiComponents() {
        addToolbar();

        // Load image
        JLabel songImage = new JLabel(loadImage("src/main/resources/record.png"));
        songImage.setBounds(0, 50, getWidth(), 225);
        add(songImage);

        // Song title
        songTitle = new JLabel("Song Title");
        songTitle.setBounds(0, 285, getWidth() - 10, 30);
        songTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        // Song artist
        songArtist = new JLabel("Artist");
        songArtist.setBounds(0, 315, getWidth() - 10, 30);
        songArtist.setFont(new Font("Dialog", Font.PLAIN, 24));
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);

        // PLayback slider
        playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBounds(getWidth()/2 - 300/2, 365, 300, 40);
        playbackSlider.setBackground(null);
        playbackSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Pause the song when the user press the slider
                musicPlayer.pauseSong();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                JSlider source = (JSlider) e.getSource();

                // Get the value from where the user wants the playback at
                int frame = source.getValue();

                // Update current frame in the musicPlayer to this frame
                musicPlayer.setCurrentFrame(frame);

                // Update current time in milliseconds
                musicPlayer.setCurrentTimeInMilliseconds((int) (frame / (2.08 * musicPlayer.getCurrentSong().getFrameRatePerMilliseconds())));

                // Resume song
                musicPlayer.playCurrentSong();

                // toggle on pause button;
                enablePauseButton();
            }
        });
        add(playbackSlider);

        // PLayback functionalities
        addPlaybackButtons();
    }

    // Update playback slider from MusicPlayer class
    public void setPlaybackSliderValue(int frame) {
        playbackSlider.setValue(frame);
    }

    private void addToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0, 0, getWidth(), 20);

        // Prevent toolbar from being moved
        toolBar.setFloatable(false);

        // Add dropdown menu
        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);

        // Song menu
        JMenu songMenu = new JMenu("Song");
        menuBar.add(songMenu);

        // Load song
        JMenuItem loadSong = getJMenuItem();
        songMenu.add(loadSong);

        // Playlist menu
        JMenu playlistMenu = new JMenu("Playlist");
        menuBar.add(playlistMenu);

        JMenuItem createPlaylist = new JMenuItem("Create Playlist");
        createPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Load music playlist dialog
                new MusicPlaylistDialog(MusicPlayerGUI.this).setVisible(true);
            }
        });
        playlistMenu.add(createPlaylist);

        JMenuItem loadPlaylist = getMenuItem();
        playlistMenu.add(loadPlaylist);

        add(toolBar);
    }

    private JMenuItem getMenuItem() {
        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        loadPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileFilter(new FileNameExtensionFilter("Playlist", "txt"));
                jFileChooser.setCurrentDirectory(new File("src/main/resources/music"));

                int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selectedFile = jFileChooser.getSelectedFile();

                if (result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                    // Stop the music
                    musicPlayer.stopSong();

                    // Load playlist
                    musicPlayer.loadPlaylist(selectedFile);
                }
            }
        });
        return loadPlaylist;
    }

    private JMenuItem getJMenuItem() {
        JMenuItem loadSong = new JMenuItem("Load Song");
        loadSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // an int is returned to let us know what the user did
                int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selectedFile = jFileChooser.getSelectedFile();

                // Song will only load if user presses open button
                if (result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                    // create a song obj based on selected file
                    Song song = new Song(selectedFile.getPath());
                    
                    // load song into music player
                    musicPlayer.loadSong(song);

                    // update song title and artist
                    updateSongTitleAndArtist(song);

                    // update playback slider
                    updatePlaybackSlider(song);

                    // Switch play to pause button
                    enablePauseButton();

                    // Switch pause to play button
                    // enablePlayButton();
                }
            }
        });
        return loadSong;
    }

    private void addPlaybackButtons(){
        playbackButtons = new JPanel();
        playbackButtons.setBounds(0, 435, getWidth() - 10, 80);
        playbackButtons.setBackground(null);

        // Mute Button
        JButton muteButton = new JButton(loadImage("src/main/resources/unmute.png"));
        muteButton.setBorderPainted(false);
        muteButton.setBackground(null);
        muteButton.addActionListener(e -> {
            musicPlayer.toggleMute();
            if (musicPlayer.isMuted()) {
                muteButton.setIcon(loadImage("src/main/resources/mute.png"));
            }
            else {
                muteButton.setIcon(loadImage("src/main/resources/unmute.png"));
            }
        });
        playbackButtons.add(muteButton);

        // Play button
        JButton playButton = new JButton(loadImage("src/main/resources/play.png"));
        playButton.setBorderPainted(false);
        playButton.setBackground(null);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enablePauseButton();
                musicPlayer.playCurrentSong();
            }
        });
        playbackButtons.add(playButton);

        // Pause button
        JButton pauseButton = new JButton(loadImage("src/main/resources/pause.png"));
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null);
        pauseButton.setVisible(false);
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enablePlayButton();
                musicPlayer.pauseSong();
            }
        }); 
        playbackButtons.add(pauseButton);

        // Next button
        JButton nextButton = new JButton(loadImage("src/main/resources/next.png"));
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                musicPlayer.playNextSong();
            }
        });
        playbackButtons.add(nextButton);

        add(playbackButtons);
    }

    public void updateSongTitleAndArtist(Song song) {
        songTitle.setText(song.getSongTitle());
        songArtist.setText(song.getSongArtist());
    }

    public void updatePlaybackSlider(Song song) {
        // Update max count for slider
        playbackSlider.setMaximum(song.getMp3File().getFrameCount());

        // Create song length label
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();

        // Beginning will be 00:00
        JLabel labelBeginning = new JLabel("00:00");
        labelBeginning.setFont(new Font("Dialog", Font.BOLD, 18));
        labelBeginning.setForeground(TEXT_COLOR);

        // End will vary depending on the song
        JLabel labelEnd = new JLabel(song.getSongLength());
        labelEnd.setFont(new Font("Dialog", Font.BOLD, 18));
        labelEnd.setForeground(TEXT_COLOR);

        labelTable.put(0, labelBeginning);
        labelTable.put(song.getMp3File().getFrameCount(), labelEnd);

        playbackSlider.setLabelTable(labelTable);
        playbackSlider.setPaintLabels(true);
    }

    public void enablePauseButton() {
        // retrieve ref to play button
        JButton playButton = (JButton) playbackButtons.getComponent(1);
        JButton pauseButton = (JButton) playbackButtons.getComponent(2);

        // Disable play button
        playButton.setVisible(false);

        // Enable pause button
        pauseButton.setVisible(true);
    }
    
    public void enablePlayButton() {
        // retrieve ref to play button
        JButton playButton = (JButton) playbackButtons.getComponent(1);
        JButton pauseButton = (JButton) playbackButtons.getComponent(2);

        // Disable pause button
        pauseButton.setVisible(false);

        // Enable play button
        playButton.setVisible(true);
    }

    private ImageIcon loadImage(String imagePath) {
        try {
            // Read image from path
            BufferedImage image = ImageIO.read(new File(imagePath));

            // return image icon to render
            return new ImageIcon(image);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
