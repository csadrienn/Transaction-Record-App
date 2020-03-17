package com.csontaka.transaction_record_app.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Displays a form to collect data for the fields for the <code>Material</code>
 * objects
 *
 * @author Adrienn Csontak
 */
public class PriceFormPanel extends JPanel {

    private JLabel nameLb;
    private JTextField nameField;
    private JLabel valueLb;
    private JTextField valueField;
    private JButton addBtn;
    private final Locale LOCALE = new Locale("en", "UK");
    private final DecimalFormat DECIMAL_FORMAT = (DecimalFormat) NumberFormat.getNumberInstance(LOCALE);

    /**
     * Constructs a PriceFormPanel with the specified name and value, that sets
     * the displayed text of the name and value labels.
     *
     * @param name A String containing the text of the name label.
     * @param value A String containing the text of the value label.
     */
    public PriceFormPanel(String name, String value) {
        DECIMAL_FORMAT.applyPattern("##0.00");
        initComponents(name, value);
        layoutComponents();
    }

    private void initComponents(String name, String price) {
        Font font = new Font("Lucida Sans Unicode", 0, 14);
        nameLb = new JLabel(name);
        nameLb.setFont(font);
        nameLb.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        nameField = new JTextField(15);
        valueLb = new JLabel(price);
        valueLb.setFont(font);
        valueLb.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        valueField = new JTextField(15);
        Icon addIcon = Utils.createIcon("/images/add20.png");
        if(addIcon != null){
            addBtn = new JButton(addIcon);
        }else{
            addBtn = new JButton("Add");
        }
        Utils.setButtonLook(addBtn, Color.GREEN.darker(), 11);
    }

    private void layoutComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints labelGc = new GridBagConstraints();
        labelGc.anchor = GridBagConstraints.WEST;
        labelGc.insets = new Insets(5, 5, 5, 5);
        labelGc.weightx = 0.0;
        labelGc.gridy = 0;

        GridBagConstraints fieldGc = (GridBagConstraints) labelGc.clone();
        fieldGc.weightx = 1.0;
        labelGc.insets = new Insets(5, 5, 5, 5);

        add(nameLb, labelGc);
        add(nameField, fieldGc);
        labelGc.gridy++;
        fieldGc.gridy++;
        add(valueLb, labelGc);
        add(valueField, fieldGc);
        labelGc.gridy++;
        labelGc.gridx = 1;
        add(addBtn, labelGc);
        add(Box.createRigidArea(new Dimension(10, 10)));

    }

    /**
     * Checks if the user's inputs are correct. If not shows a warning message
     * in a dialog window.
     *
     * @return True if the input is correct, false if it is not.
     */
    public boolean checkValidity() {
        String sep = DECIMAL_FORMAT.getDecimalFormatSymbols().getDecimalSeparator() + "";
        String name = nameField.getText().trim();
        String priceText = valueField.getText().trim();
        if (name.equals("") || priceText.equals("")) {
            Utils.showWarningMessage(getRootPane(), "The fields can not be empty!");
            return false;
        }
        String price = priceText.replace(sep, "");
        if (!price.matches("^[0-9]+$")) {
            Utils.showWarningMessage(getRootPane(), "Please enter a valid price!");
            return false;
        }
        int priceInt = Integer.parseInt(price);
        if (priceInt == 0) {
            Utils.showWarningMessage(getRootPane(), "The price can not be 0.");
            return false;
        }
        return true;
    }

    /**
     * Gets the text of the name field.
     *
     * @return A String representing the text of the name field.
     */
    public String getNameFieldText() {
        return nameField.getText();
    }

    /**
     * Sets the text of the name field.
     *
     * @param text A String containing the text of the name field.
     */
    public void setNameFieldText(String text) {
        nameField.setText(text);
    }

    /**
     * Gets the text of the name field.
     *
     * @return A String representing the text of the value field.
     */
    public String getValueFieldText() {
        return valueField.getText();
    }

    /**
     * Sets the text of the name field.
     *
     * @param text A String containing the text of the value field.
     */
    public void setValueFieldText(String text) {
        valueField.setText(text);
    }

    /**
     * Gets the add button of the form.
     *
     * @return The JButton object of the form.
     */
    public JButton getAddBtn() {
        return addBtn;
    }
}
