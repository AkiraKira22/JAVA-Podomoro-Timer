package application.util;

import javax.swing.JOptionPane;

public class DNDUtil {

    public static void enableDND() {
        try {
            new ProcessBuilder("cmd", "/c", "start", "ms-settings:notifications").start();
            // Show a dialog to instruct the user
            JOptionPane.showMessageDialog(null,
                "Please enable Do Not Disturb manually in the Setting.",
                "Enable Do Not Disturb",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (Exception e) { 
            e.printStackTrace();
        }
    }

    public static void disableDND() {
        JOptionPane.showMessageDialog(null,
            "Please disable Do Not Disturb manually in the Setting.",
            "Disable Do Not Disturb",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}