package com.csontaka.transaction_record_app.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * A utility class for the project.
 *
 * @author Adrienn Csontak
 */
public class Utils {

    /**
     * Creates an <code>ImagesIcon</code> object based on the specified path.
     *
     * @param path A String containing the path of the icon.
     * @return An <code>ImagesIcon</code> object if the icon is found on the
     * given path, else returns null.
     */
    public static ImageIcon createIcon(String path) {
        URL url = Utils.class.getResource(path);
        if (url == null) {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
        return new ImageIcon(url);
    }

    /**
     * Sets the specified button look. Sets the background colour to the general
     * and the font to the specified font size.
     *
     * @param button A <code>JButton</code> object to set it's look.
     * @param fontSize An int containing the size of the font used on the
     * button.
     */
    public static void setButtonLook(JButton button, int fontSize) {
        setButtonLook(button, new Color(65, 105, 225), fontSize);
    }

    /**
     * Sets the specified button look. Sets the background colour to the
     * specified and the font to the specified font size.
     *
     * @param button A <code>JButton</code> object to set it's look.
     * @param backGround A <code>Color</code> object used for setting the
     * buttons background colour.
     * @param fontSize An int containing the size of the font used on the
     * button.
     */
    public static void setButtonLook(JButton button, Color backGround, int fontSize) {
        button.setFont(new Font("Lucida Sans Unicode", Font.BOLD, fontSize));
        button.setBackground(backGround);
        button.setForeground(Color.WHITE);
        Border out = BorderFactory.createBevelBorder(BevelBorder.RAISED);
        Border in = BorderFactory.createEmptyBorder(3, 13, 3, 13);
        button.setBorder(BorderFactory.createCompoundBorder(out, in));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    /**
     * Shows a warning message in a dialog window with an a specified parent
     * component and message.
     *
     * @param parent The parent <code>Component</code> for the dialog.
     * @param message A String containing the message displayed by the dialog.
     */
    public static void showWarningMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message,
                "Error", JOptionPane.WARNING_MESSAGE);
    }

}
