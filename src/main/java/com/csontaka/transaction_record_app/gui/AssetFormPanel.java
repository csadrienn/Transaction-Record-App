package com.csontaka.transaction_record_app.gui;

import com.csontaka.transaction_record_app.entity.AssetType;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Creates a form elements for setting a new asset or updating an existing one.
 *
 * @author Adrienn Csontak
 */
public class AssetFormPanel extends JPanel {

    private AssetType type;
    private JLabel nameLabel;
    private JLabel descLabel;
    private JLabel stockLabel;
    private JLabel priceLabel;

    private JTextField nameField;
    private JTextArea descArea;
    private JScrollPane descScrollPane;
    private JTextField stockField;
    private JTextField priceField;
    private final Locale LOCALE = new Locale("en", "UK");
    private final DecimalFormat DECIMAL_FORMAT = (DecimalFormat) NumberFormat.getNumberInstance(LOCALE);

    /**
     * Construct a AssetFormPanel with a specified assetType and a boolean value
     * for setting the text fields and area to editable or not.
     *
     * @param type An <code>AssetType</code> object defining that the form is
     * for products or equipment.
     * @param editable If true the text fields and area are editable, if false
     * they are not editable.
     */
    public AssetFormPanel(AssetType type, boolean editable) {
        this.type = type;
        DECIMAL_FORMAT.applyPattern("##0.00");
        componentSetUp(editable);
        setUpLayout();
    }

    private void componentSetUp(boolean editable) {
        nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Lucida Sans Unicode", 0, 14));
        descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Lucida Sans Unicode", 0, 14));
        stockLabel = new JLabel("Stock:");
        stockLabel.setFont(new Font("Lucida Sans Unicode", 0, 14));
        if (type.equals(AssetType.PRODUCT)) {
            priceLabel = new JLabel("Material cost:");
        } else {
            priceLabel = new JLabel("Usual price");
        }
        priceLabel.setFont(new Font("Lucida Sans Unicode", 0, 14));

        nameField = new JTextField(15);
        descArea = new JTextArea(3, 15);
        descScrollPane = new JScrollPane(descArea);
        stockField = new JTextField(15);
        priceField = new JTextField(15);

        setTextFieldsEditable(editable);
    }

    private void setUpLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.fill = GridBagConstraints.BOTH;
        Insets ins = new Insets(5, 5, 5, 5);
        gc.insets = ins;
        gc.weightx = 0.0;
        gc.gridwidth = GridBagConstraints.RELATIVE;
        add(nameLabel, gc);
        gc.gridwidth = GridBagConstraints.REMAINDER;
        add(nameField, gc);
        gc.gridwidth = GridBagConstraints.RELATIVE;
        add(descLabel, gc);
        gc.gridwidth = GridBagConstraints.REMAINDER;
        add(descArea, gc);
        gc.gridwidth = GridBagConstraints.RELATIVE;
        add(stockLabel, gc);
        gc.gridwidth = GridBagConstraints.REMAINDER;
        add(stockField, gc);
        gc.gridwidth = GridBagConstraints.RELATIVE;
        add(priceLabel, gc);
        gc.gridwidth = GridBagConstraints.REMAINDER;
        add(priceField, gc);
    }

    /**
     * Sets the text fields and area to editable or not.
     *
     * @param editable If true the text fields and area are editable, if false
     * they are not editable.
     */
    public void setTextFieldsEditable(boolean editable) {
        nameField.setEditable(editable);
        descArea.setEditable(editable);
        stockField.setEditable(editable);
        priceField.setEditable(editable);
    }

    /**
     * Clears the text fields and area or sets to default.
     *
     */
    public void clearTextFields() {
        nameField.setText("");
        descArea.setText("");
        stockField.setText("");
        priceField.setText("0.00");
    }

    /**
     * Checks the user input. The input is correct if the name and the stocks
     * fields are not empty, the number values are corrects and the stock value
     * is a positive number. If the inputs are not correct shows a warning
     * message.
     *
     * @return True if the user input is correct, false if it is not.
     */
    public boolean checkInput() {
        String sep = DECIMAL_FORMAT.getDecimalFormatSymbols().getDecimalSeparator() + "";
        String name = nameField.getText().trim();
        String desc = descArea.getText().trim();
        String stockText = stockField.getText().trim();
        String priceText = priceField.getText().trim();
        if (priceText.equals("")) {
            priceText = "0" + sep + "00";
            priceField.setText(priceText);
        }
        priceText = priceText.replace(sep, "");
        if (name.equals("") || stockText.equals("")) {
            Utils.showWarningMessage(getRootPane(), "The name and the stock field can not be empty!");
            return false;
        }
        if (!priceText.matches("^[0-9]+$") || !stockText.matches("^[-]?[0-9]+$")) {
            Utils.showWarningMessage(getRootPane(), "Please enter a valid number!");
            return false;
        }
        int stock = Integer.parseInt(stockText);
        if (stock < 0) {
            Utils.showWarningMessage(getRootPane(), "Please enter a positive stock number!");
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
     * Gets the text of the description area.
     *
     * @return A String representing the text of the description area.
     */
    public String getDescAreaText() {
        return descArea.getText();
    }

    /**
     * Sets the text of the description area.
     *
     * @param text A String containing the text of the description area.
     */
    public void setDescAreaText(String text) {
        descArea.setText(text);
    }

    /**
     * Gets the text of the stock field.
     *
     * @return A String representing the text of the stock field.
     */
    public String getStockFieldText() {
        return stockField.getText();
    }

    /**
     * Sets the text of the stock field.
     *
     * @param text A String containing the text of the stock field.
     */
    public void setStockFieldText(String text) {
        stockField.setText(text);
    }

    /**
     * Gets the text of the price field.
     *
     * @return A String representing the text of the price field.
     */
    public String getPriceFieldText() {
        return priceField.getText();
    }

    /**
     * Sets the text of the price field.
     *
     * @param text A String containing the text of the price field.
     */
    public void setPriceFieldText(String text) {
        priceField.setText(text);
    }

}
