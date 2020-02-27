package com.csontaka.transaction_record_app.gui;

import com.csontaka.transaction_record_app.entity.Asset;
import com.csontaka.transaction_record_app.entity.AssetType;
import com.csontaka.transaction_record_app.entity.Period;
import com.csontaka.transaction_record_app.entity.Transaction;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * Creates a form for the user to save a new Transaction or update an existing
 * one. Also creates a form in a dialog to save a new Product if it is needed.
 *
 * @author Adri
 */
public class TransactionFormPanel extends JPanel implements ItemListener {

    private final String SELECTED_ASSET_OPTION = "Add new item";
    private JLabel titleLabel;
    private JPanel formPanel;
    private JLabel assetLabel;
    private JComboBox assetCombo;
    private JLabel dateLabel;
    private JComboBox dateCombo;
    private JLabel priceLabel;
    private JTextField priceTextField;
    private JLabel currencyLabel;
    private JLabel amountLabel;
    private JTextField amountTextField;
    private JLabel successLabel;
    private JButton saveButton;
    private JButton leaveButton;
    private final String PATTERN = "yyyy/MM";
    private final DateTimeFormatter DF;
    private final Locale LOCALE = new Locale("en", "UK");
    private final DecimalFormat DECIMAL_FORMAT = (DecimalFormat) NumberFormat.getNumberInstance(LOCALE);
    private FormListener transactionFormListener;
    private Transaction transToSave;
    private AssetType assetType;
    private List<Period> periodList;
    private List<Asset> assetList;

    //dialog
    private JLabel nameLabel;
    private JTextField nameField;
    private JTextArea featureArea;
    private JScrollPane featureScrollPane;
    private JLabel featureLabel;
    private JDialog newAssetDialog;
    private JButton okBtn;
    private JButton cancelBtn;
    private JPanel buttonPanel;
    private Asset assetToSave;

    /**
     * Construct a TransactionFormPanel with a specified assetType that define
     * the transactions displayed in the table.
     *
     * @param assetType The AssetType enum that define the type of assets.
     */
    public TransactionFormPanel(AssetType assetType) {
        DECIMAL_FORMAT.applyPattern("##0.00");
        this.assetType = assetType;

        periodList = new ArrayList<>();
        assetList = new ArrayList<>();
        DF = DateTimeFormatter.ofPattern(PATTERN);
        formPanel = new JPanel();
        titleLabel = new JLabel("Transaction form");
        assetLabel = new JLabel("Product:");
        dateLabel = new JLabel("Date:");
        priceLabel = new JLabel("Price:");
        priceTextField = new JTextField(10);
        currencyLabel = new JLabel("£");
        amountLabel = new JLabel("Amount:");
        amountTextField = new JTextField(10);
        successLabel = new JLabel("Saving succesful");
        successLabel.setVisible(false);
        saveButton = new JButton("Save");
        leaveButton = new JButton("Cancel");
        transToSave = null;

        dateCombo = new JComboBox(new String[0]);
        dateCombo.setPrototypeDisplayValue("0000/00");
        assetCombo = new JComboBox(new String[0]);
        assetCombo.setPrototypeDisplayValue("a pretty long name(000)");
        assetCombo.addItemListener(this);

        addActionListenersToButtons();
        layoutComponents();
        setUpHiddenDialog();

    }

    /**
     * Prepares the form for updating. Fills the form fields and sets the combo
     * boxes with the received data.
     *
     * @param asset An Asset containing the asset name and id to set the combo
     * box.
     * @param period A Period containing the year-month to set the combo box.
     * @param transaction A Transaction containing the amount and price to fill
     * the text fields.
     */
    public void setUpForUpdate(Asset asset, Period period, Transaction transaction) {
        transToSave = transaction;
        assetCombo.setSelectedItem(asset.getName() + "(" + asset.getId() + ")");
        dateCombo.setSelectedItem(DF.format(period.getDate()));
        amountTextField.setText(transaction.getAmount() + "");
        double price = transaction.getPrice() / 100.0;
        priceTextField.setText(DECIMAL_FORMAT.format(price));
        resetAssetToSave();
    }

    /**
     * Prepares the form for adding new Transaction. Clears the text field. Set
     * the asset combo box to the first item and the date combo box to the last
     * item.
     *
     */
    public void setUpForNew() {
        transToSave = null;
        assetCombo.setSelectedIndex(0);
        int last = dateCombo.getItemCount() - 1;
        dateCombo.setSelectedIndex(last);
        amountTextField.setText("");
        priceTextField.setText("");
        resetAssetToSave();
    }

    /**
     * Sets the transactionFormListener class member.
     *
     * @param transactionFormListener A FormListener to set the
     * transactionFormListener member of the class.
     */
    public void setTransactionFormListener(FormListener transactionFormListener) {
        this.transactionFormListener = transactionFormListener;
    }

    /**
     * Sets the title label of the form. Fills the combo boxes with data to
     * create the items.
     *
     * @param periods A List of Period objects to use the year-month values as
     * items.
     * @param assets A List of Asset objects to use the name and the id values
     * as items.
     */
    public void setUpComboBoxesAndTitle(List<Period> periods, List<Asset> assets) {
        if (assetType.equals(AssetType.PRODUCT)) {
            titleLabel.setText("Income form");
        } else {
            titleLabel.setText("Expense form");
        }
        setPeriodList(periods);
        setAssetList(assets);
        refreshComboBoxes();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == assetCombo) {
            String selected = (String) assetCombo.getSelectedItem();
            if (selected.equals(SELECTED_ASSET_OPTION)) {
                newAssetDialog.setVisible(true);
            }
        }
    }

    private void resetAssetToSave() {
        assetToSave = null;
    }

    private void addActionListenersToButtons() {
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                YearMonth date = YearMonth.parse((String) dateCombo.getSelectedItem(), DF);
                Period period = periodList.stream()
                        .filter(p -> p.getDate().equals(date))
                        .findAny()
                        .orElse(null);
                try {
                    int amount = Integer.parseInt(amountTextField.getText());
                    String sep = DECIMAL_FORMAT.getDecimalFormatSymbols().getDecimalSeparator() + "";
                    String priceStr = priceTextField.getText().replace(sep, "");
                    int price = Integer.parseInt(priceStr);
                    FormEvent event = new FormEvent(this);

                    if (transToSave == null) {
                        transToSave = new Transaction(amount, price);
                        transToSave.setPeriodId(period.getId());
                    } else {
                        transToSave.setAmount(amount);
                        transToSave.setPrice(price);
                        transToSave.setPeriodId(period.getId());
                    }
                    if (assetToSave == null) {
                        String selected = (String) assetCombo.getSelectedItem();
                        int startOfNum = selected.indexOf('(') + 1;
                        int assetId = Integer.parseInt(selected.substring(startOfNum, selected.length() - 1));
                        Asset asset = assetList.stream()
                                .filter(a -> a.getId() == assetId)
                                .findAny()
                                .orElse(null);

                        transToSave.setAssetId(assetId);

                    } else {
                        event.setAsset(assetToSave);
                    }
                    event.setTransaction(transToSave);

                    if (transactionFormListener != null) {
                        transactionFormListener.formEventOccured(event);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(getRootPane(),
                            "Please enter a valid number!",
                            "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        leaveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                FormEvent event = new FormEvent(e);
                if (transactionFormListener != null) {
                    transactionFormListener.formEventOccured(event);
                }
            }
        });
    }

    private void setPeriodList(List<Period> periods) {
        periodList = periods;
    }

    private void setAssetList(List<Asset> assets) {
        assetList = new ArrayList<>();
        if (assetType.equals(assetType.PRODUCT)) {
            for (Asset asset : assets) {
                if (asset.getType() == assetType.PRODUCT) {
                    assetList.add(asset);
                }
            }
        } else {
            for (Asset asset : assets) {
                if (asset.getType() == assetType.EQUIPMENT) {
                    assetList.add(asset);
                }
            }
        }
    }

    private void refreshComboBoxes() {
        if (periodList.size() != dateCombo.getItemCount()) {
            dateCombo.removeAllItems();
            for (Period period : periodList) {
                dateCombo.addItem(DF.format(period.getDate()));
            }
            dateCombo.setSelectedIndex(dateCombo.getItemCount() - 1);
        }
        if (assetList.size() != assetCombo.getItemCount() - 1) {
            assetCombo.removeAllItems();
            for (Asset asset : assetList) {
                String item = asset.getName() + "(" + asset.getId() + ")";
                assetCombo.addItem(item);
            }
            assetCombo.addItem(SELECTED_ASSET_OPTION);
        }
    }

    private void layoutComponents() {
        titleLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 24)); // NOI18N
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        Border innerBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
        Border outerBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        formPanel.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

        setFonts();
        setFormPanelLayout();

        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
    }

    private void setFonts() {
        assetLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14)); // NOI18N
        assetLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        dateLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14)); // NOI18N
        dateLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        priceLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14)); // NOI18N
        priceLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        currencyLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14)); // NOI18N
        currencyLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        amountLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14)); // NOI18N
        amountLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        successLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12)); // NOI18N
        successLabel.setHorizontalAlignment(SwingConstants.CENTER);

        assetCombo.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14));
        dateCombo.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14)); // NOI18N

        saveButton.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14));
        saveButton.setPreferredSize(new Dimension(80, 35));
        leaveButton.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14));
        leaveButton.setPreferredSize(new Dimension(80, 35));
    }

    private void setFormPanelLayout() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        formPanel.setLayout(gridBagLayout);

        GridBagConstraints gbc = new GridBagConstraints();
        Insets firstColumnInsets = new Insets(0, 0, 15, 30);
        Insets secondColumnInsets = new Insets(0, 0, 15, 0);
        gbc.gridy = 0;

        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 0;
        gbc.insets = firstColumnInsets;
        formPanel.add(assetLabel, gbc);

        gbc.gridx = 1;
        gbc.insets = secondColumnInsets;
        formPanel.add(assetCombo, gbc);

        //next row
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets = firstColumnInsets;
        formPanel.add(dateLabel, gbc);

        gbc.gridx = 1;
        gbc.insets = secondColumnInsets;
        formPanel.add(dateCombo, gbc);

        //next row
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets = firstColumnInsets;
        formPanel.add(priceLabel, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, -10, 15, 0);
        formPanel.add(currencyLabel, gbc);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.insets = new Insets(0, -199, 15, 0);
        formPanel.add(priceTextField, gbc);

        //next row
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets = firstColumnInsets;
        formPanel.add(amountLabel, gbc);

        gbc.gridx = 1;
        gbc.insets = secondColumnInsets;
        formPanel.add(amountTextField, gbc);

        //next row
        gbc.gridy++;
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(successLabel, gbc);

        //next row
        gbc.gridy++;
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(saveButton, gbc);

        gbc.insets = new Insets(0, 0, 15, 30);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(leaveButton, gbc);
    }

    private void setUpHiddenDialog() {
        newAssetDialog = new JDialog();
        newAssetDialog.setTitle("New asset form");
        newAssetDialog.setSize(550, 300);

        nameLabel = new JLabel("Name:");
        nameLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14));
        featureArea = new JTextArea(3, 30);
        featureScrollPane = new JScrollPane(featureArea);
        featureLabel = new JLabel("Feature:");
        featureLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14));
        nameField = new JTextField(15);

        buttonPanel = new JPanel();
        okBtn = new JButton("OK");
        cancelBtn = new JButton("Cancel");
        setUpDialogBtns();

        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        setDialogLayout();

    }

    private void setDialogLayout() {
        newAssetDialog.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        newAssetDialog.add(nameLabel, c);
        c.gridx = 1;
        c.gridy = 0;
        newAssetDialog.add(nameField, c);
        c.gridx = 0;
        c.gridy = 1;
        newAssetDialog.add(featureLabel, c);
        c.gridx = 1;
        c.gridy = 1;
        newAssetDialog.add(featureScrollPane, c);

        c.anchor = GridBagConstraints.LAST_LINE_END;
        c.gridx = 1;
        c.gridy = 2;
        newAssetDialog.add(buttonPanel, c);
    }

    private void setUpDialogBtns() {
        okBtn.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14));
        okBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                newAssetDialog.setVisible(false);
                assetToSave = new Asset(nameField.getText(), featureArea.getText(), assetType);
                assetCombo.removeItem(SELECTED_ASSET_OPTION);
                String createdItem = "New product: " + assetToSave.getName();
                assetCombo.addItem(createdItem);
                assetCombo.setSelectedItem(createdItem);
                nameField.setText("");
                featureArea.setText("");
            }
        });
        cancelBtn.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14));
        cancelBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                newAssetDialog.setVisible(false);
                nameField.setText("");
                featureArea.setText("");
            }
        });
    }
}
