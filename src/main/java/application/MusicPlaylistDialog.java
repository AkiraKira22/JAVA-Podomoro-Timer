package application;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MusicPlaylistDialog extends JDialog{

    // Store all the paths to be written to a txt file (when we load a playlist)
    private final ArrayList<String> songPaths;

    public MusicPlaylistDialog(MusicPlayerGUI musicPlayerGUI) {
        songPaths = new ArrayList<>();

        // Configure dialog
        setTitle("Create Playlist");
        setSize(400, 400);
        setResizable(false);
        getContentPane().setBackground(MusicPlayerGUI.FRAME_COLOR);
        setLayout(null);
        setModal(true); // This property makes it so thet the dialog has to be closed to give focus
        setLocationRelativeTo(musicPlayerGUI);

        addDialogComponents();
    }

    private void addDialogComponents() {
        // Container to hold each song path
        JPanel songContainer = new JPanel();
        songContainer.setLayout(new BoxLayout(songContainer, BoxLayout.Y_AXIS));
        songContainer.setBounds((int)(getWidth() * 0.025), 10, (int)(getWidth() * 0.90), (int)(getHeight() * 0.75));
        add(songContainer);

        // Add song button
        JButton addSongButton = getJButton(songContainer);
        add(addSongButton);

        // Save playlist button
        JButton savePlaylistButton = new JButton("Save");
        savePlaylistButton.setBounds(215, (int)(getHeight() * 0.80), 100, 25);
        savePlaylistButton.setFont(new Font("Dialog", Font.BOLD, 14));
        savePlaylistButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setCurrentDirectory(new File("src/main/resources/music"));
                    int result = jFileChooser.showSaveDialog(MusicPlaylistDialog.this);

                    if (result == JFileChooser.APPROVE_OPTION) {
                        // Use getSelectedFile() to get reference to the file that we are about to save
                        BufferedWriter bufferedWriter = getBufferedWriter(jFileChooser);
                        bufferedWriter.close();

                        // Display success dialog
                        JOptionPane.showMessageDialog(MusicPlaylistDialog.this, "Successfully Created Playlist!");

                        // Close the dialog
                        MusicPlaylistDialog.this.dispose();
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            private BufferedWriter getBufferedWriter(JFileChooser jFileChooser) throws IOException {
                FileWriter fileWriter = getFileWriter(jFileChooser);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                // Iterate through the song paths list and write each string into the file
                // Each song will be written in their own row
                for (String songPath : songPaths) {
                    bufferedWriter.write(songPath + "\n");
                }
                return bufferedWriter;
            }

            private FileWriter getFileWriter(JFileChooser jFileChooser) throws IOException {
                File selectedFile = jFileChooser.getSelectedFile();

                // Convert to .txt if not yet already
                // Check if file is in .txt format
                if (!selectedFile.getName().substring(selectedFile.getName().length() - 4).equalsIgnoreCase(".txt")) {
                    selectedFile = new File(selectedFile.getAbsoluteFile() + ".txt");
                }

                // Create the new file at the designated directory
                selectedFile.createNewFile();

                // Write all the song paths into this file
                return new FileWriter(selectedFile);
            }
        });
        add(savePlaylistButton);
    }

    private JButton getJButton(JPanel songContainer) {
        JButton addSongButton = new JButton("Add");
        addSongButton.setBounds(60, (int)(getHeight() * 0.80), 100, 25);
        addSongButton.setFont(new Font("Dialog", Font.BOLD, 14));
        addSongButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open file explorer
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));
                jFileChooser.setCurrentDirectory(new File("src/main/resources/music"));
                int result = jFileChooser.showOpenDialog(MusicPlaylistDialog.this);

                File selectedFile = jFileChooser.getSelectedFile();
                if (result == JFileChooser.APPROVE_OPTION && selectedFile != null) {
                    JLabel filePathLabel = new JLabel(selectedFile.getPath());
                    filePathLabel.setFont(new Font("Dialog", Font.BOLD, 12));
                    filePathLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    // Add to the list
                    songPaths.add(filePathLabel.getText());

                    // Add to container
                    songContainer.add(filePathLabel);

                    // Refreshes dialog to show newly added JLabel
                    songContainer.revalidate();
                }
            }
        });
        return addSongButton;
    }
}
