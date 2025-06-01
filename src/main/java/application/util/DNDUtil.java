package application.util;
import java.awt.*;
//import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class DNDUtil {

    public static void enableDND() {
        try {
            Robot robot = new Robot();
            // Simulate the key press for enabling Do Not Disturb
            robot.keyPress(KeyEvent.VK_WINDOWS);
            robot.keyPress(KeyEvent.VK_A);
            robot.keyRelease(KeyEvent.VK_A);
            robot.keyRelease(KeyEvent.VK_WINDOWS);
            // Add a delay to allow the action to complete
            Thread.sleep(1000);
            // Simulate the key press for toggling DND
            robot.keyPress(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_TAB);
            robot.keyPress(KeyEvent.VK_SPACE);
            robot.keyRelease(KeyEvent.VK_SPACE);
        } catch (AWTException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void disableDND() {
        // Similar implementation to enableDND, but may require different key presses
        // depending on the operating system and settings.
        enableDND(); // Placeholder for actual implementation
    }
}